package com.ossprj.transmission.task;

import com.ossprj.transmission.facade.GetSessionId;
import com.ossprj.transmission.facade.ListTorrents;
import com.ossprj.transmission.facade.StartTorrents;
import com.ossprj.transmission.model.Torrent;
import com.ossprj.transmission.model.TorrentStatus;
import com.ossprj.transmission.task.model.component.Strategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public class ReseedTorrentTask {

    final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    final GetSessionId getSessionId = new GetSessionId();
    final ListTorrents listTorrents = new ListTorrents(Arrays.asList(
            "activityDate",
            "addedDate",
            "dateCreated",
            "hashString",
            "id",
            "name",
            "status",
            "trackerStats"
    ));
    final StartTorrents startTorrents = new StartTorrents();

    private List<Predicate<Torrent>> buildPredicates(final ReseedTorrentTaskConfiguration configuration) {
        return configuration.getPredicates().stream().map(predicateConfiguration -> {
            try {
                return (Predicate<Torrent>) Class.forName("com.ossprj.transmission.task.model.predicate." + predicateConfiguration.getName() + "Predicate")
                        .getConstructor(Map.class)
                        .newInstance(predicateConfiguration.getConfig());
            } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
    }

    private List<Comparator<Torrent>> buildComparators(final ReseedTorrentTaskConfiguration configuration) {
        return configuration.getComparators().stream().map(comparatorConfiguration -> {
            try {
                return (Comparator<Torrent>) Class.forName("com.ossprj.transmission.task.model.comparator." + comparatorConfiguration.getName() + "Comparator")
                        .getConstructor(Map.class)
                        .newInstance(comparatorConfiguration.getConfig());
            } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
    }

    private List<Torrent> applyStrategy(final Strategy strategy, final List<Torrent> torrents) {
        List<Torrent> filteredAndSortedTorrents = torrents;

        // Apply Filters if present
        if (strategy.getPredicate() != null) {
            filteredAndSortedTorrents = filteredAndSortedTorrents.stream().filter(strategy.getPredicate()).collect(Collectors.toList());
        }

        // Apply Comparator if present
        if (strategy.getComparator() != null) {
            filteredAndSortedTorrents = filteredAndSortedTorrents.stream().sorted(strategy.getComparator()).collect(Collectors.toList());
        }

        return filteredAndSortedTorrents;
    }

    @Bean
    public CommandLineRunner commandLineRunner(final ReseedTorrentTaskConfiguration configuration) {

        logger.info("configuration: " + configuration);

        final List<Predicate<Torrent>> predicates = buildPredicates(configuration);
        logger.debug("predicates: " + predicates);

        final List<Comparator<Torrent>> comparators = buildComparators(configuration);
        logger.debug("comparators: " + comparators);

        return (strings) -> {
            logger.debug("Getting sessionId");
            final String sessionId = getSessionId.perform(configuration.getEndpoint());

            logger.debug("Getting current Torrent list");
            final List<Torrent> torrents = listTorrents.perform(configuration.getEndpoint(), sessionId);

            logger.debug("Computing active Torrents");
            final Integer activeTorrents = torrents.stream()
                    .filter(torrent -> !torrent.getStatus().equals(TorrentStatus.STOPPED))
                    .collect(Collectors.toList()).size();

            logger.info("Torrents       (Total): " + torrents.size());
            logger.info("Torrents  (Max Active): " + configuration.getMaxActiveTorrents());
            logger.info("Torrents      (Active): " + activeTorrents);

            final Integer restartableTorrentCount;
            if (activeTorrents < configuration.getMaxActiveTorrents()) {
                restartableTorrentCount = configuration.getMaxActiveTorrents() - activeTorrents;
            } else {
                restartableTorrentCount = 0;
            }

            logger.info("Torrents (Restartable): " + restartableTorrentCount);

            if (restartableTorrentCount > 0) {
                final Strategy strategy = new Strategy(predicates, comparators);

                final List<Torrent> filteredTorrents = applyStrategy(
                        strategy,
                        torrents.stream().filter(torrent -> torrent.getStatus().equals(TorrentStatus.STOPPED)).collect(Collectors.toList()));

                logger.info("Torrents    (Selected): " + filteredTorrents.size());

                // If we have slots to fill AND there are torrents to restart
                if (!filteredTorrents.isEmpty()) {
                    final List<Torrent> torrentsToBeRestarted;
                    if (filteredTorrents.size() < restartableTorrentCount) {
                        torrentsToBeRestarted = filteredTorrents;
                    } else {
                        torrentsToBeRestarted = filteredTorrents.subList(0, restartableTorrentCount);
                    }
                    for (final Torrent torrent : torrentsToBeRestarted) {
                        logger.info("Restarting: " + torrent.getName() + " - " + torrent.getTrackerStats().get(0).getSeederCount() + " seeders, " + torrent.getTrackerStats().get(0).getLeecherCount() + " leechers");
                        if (!configuration.getDryRun()) {
                            startTorrents.perform(configuration.getEndpoint(), sessionId, torrent.getHashString());
                        }
                    }
                }
            }

        };

    }
}
