package com.ossprj.transmission.task.model.comparator;

import com.ossprj.transmission.model.Torrent;
import com.ossprj.transmission.task.model.component.AbstractComparator;

import java.util.Map;

public class LeecherCountComparator extends AbstractComparator<CountBasedComparatorConfiguration> {

    public LeecherCountComparator(final Map<String, Object> configMap) {
        super(configMap);
    }

    @Override
    protected int doCompare(final Torrent a, final Torrent b) {
        if (ComparatorSortOrder.ASCENDING.equals(configuration.getSortOrder())) {
            return a.getTrackerStats().get(0).getLeecherCount().compareTo(b.getTrackerStats().get(0).getLeecherCount());
        } else {
            return b.getTrackerStats().get(0).getLeecherCount().compareTo(a.getTrackerStats().get(0).getLeecherCount());
        }
    }

    @Override
    public String toString() {
        return "LeecherCount(" + configuration.getSortOrder() + ")";
    }
}
