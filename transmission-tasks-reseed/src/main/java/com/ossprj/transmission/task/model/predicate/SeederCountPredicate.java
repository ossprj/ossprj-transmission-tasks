package com.ossprj.transmission.task.model.predicate;

import com.ossprj.transmission.model.Torrent;
import com.ossprj.transmission.task.model.component.AbstractPredicate;

import java.util.Map;

public class SeederCountPredicate extends AbstractPredicate<CountBasedPredicateConfiguration> {

    public SeederCountPredicate(Map<String, Object> configMap) {
        super(configMap);
    }

    @Override
    public boolean doTest(Torrent torrent) {
        final Integer seederCount = torrent.getTrackerStats().get(0).getSeederCount();

        if (configuration.getMinimum() != null && seederCount < configuration.getMinimum()) {
            return false;
        }

        if (configuration.getMaximum() != null && seederCount > configuration.getMaximum()) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "SeederCount(" + configuration + ")";
    }
}