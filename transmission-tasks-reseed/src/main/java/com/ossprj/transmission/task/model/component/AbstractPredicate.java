package com.ossprj.transmission.task.model.component;

import com.ossprj.transmission.model.Torrent;

import java.util.Map;
import java.util.function.Predicate;

public abstract class AbstractPredicate<T> extends AbstractComponent<T> implements Predicate<Torrent> {

    public AbstractPredicate(final Map<String, Object> configMap) {
        super(configMap);
    }

    @Override
    public boolean test(final Torrent torrent) {
        return doTest(torrent);
    }

    protected abstract boolean doTest(final Torrent torrent);
}
