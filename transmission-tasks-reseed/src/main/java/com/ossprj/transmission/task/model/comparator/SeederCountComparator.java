package com.ossprj.transmission.task.model.comparator;

import com.ossprj.transmission.model.Torrent;
import com.ossprj.transmission.task.model.component.AbstractComparator;

import java.util.Map;

public class SeederCountComparator extends AbstractComparator<CountBasedComparatorConfiguration> {

    public SeederCountComparator(final Map<String, Object> configMap) {
        super(configMap);
    }

    @Override
    protected int doCompare(final Torrent a, final Torrent b) {
        if (ComparatorSortOrder.ASCENDING.equals(configuration.getSortOrder())) {
            return a.getTrackerStats().get(0).getSeederCount().compareTo(b.getTrackerStats().get(0).getSeederCount());
        } else {
            return b.getTrackerStats().get(0).getSeederCount().compareTo(a.getTrackerStats().get(0).getSeederCount());
        }
    }

    @Override
    public String toString() {
        return "SeederCount(" + configuration.getSortOrder() + ")";
    }
}
