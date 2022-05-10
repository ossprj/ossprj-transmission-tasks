package com.ossprj.transmission.task.model.predicate;

import com.ossprj.transmission.model.Torrent;
import com.ossprj.transmission.task.model.component.AbstractPredicate;

import java.util.Map;

public class LeecherCountPredicate extends AbstractPredicate<CountBasedPredicateConfiguration> {

    public LeecherCountPredicate(Map<String, Object> configMap) {
        super(configMap);
    }

    @Override
    public boolean doTest(Torrent torrent) {
        final Integer leecherCount = torrent.getTrackerStats().get(0).getLeecherCount();

        if (configuration.getMinimum() != null && leecherCount < configuration.getMinimum()) {
            return false;
        }

        if (configuration.getMaximum() != null && leecherCount > configuration.getMaximum()) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "LeecherCount(" + configuration + ")";
    }
}
