package com.ossprj.transmission.task.model.component;

import com.ossprj.transmission.model.Torrent;

import java.util.Comparator;
import java.util.Map;

public abstract class AbstractComparator<T> extends AbstractComponent<T> implements Comparator<Torrent> {

    public AbstractComparator(final Map<String, Object> configMap) {
        super(configMap);
    }

    @Override
    public int compare(final Torrent a, final Torrent b) {
        return doCompare(a, b);
    }

    protected abstract int doCompare(final Torrent a, final Torrent b);

}
