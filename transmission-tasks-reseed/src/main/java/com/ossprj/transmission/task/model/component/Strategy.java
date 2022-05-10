package com.ossprj.transmission.task.model.component;

import com.ossprj.transmission.model.Torrent;
import lombok.Data;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

@Data
public class Strategy {

    private final Predicate<Torrent> predicate;
    private final Comparator<Torrent> comparator;

    public Strategy(final List<Predicate<Torrent>> predicates, final List<Comparator<Torrent>> comparators) {
        if (predicates != null && !predicates.isEmpty()) {
            predicate = predicates.stream().reduce(Predicate::and).get();
        } else {
            predicate = null;
        }

        if (comparators != null && !comparators.isEmpty()) {
            comparator = comparators.stream().reduce(Comparator::thenComparing).get();
        } else {
            comparator = null;
        }

    }

}
