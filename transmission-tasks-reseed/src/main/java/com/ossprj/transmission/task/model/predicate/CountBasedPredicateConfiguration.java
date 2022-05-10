package com.ossprj.transmission.task.model.predicate;

import lombok.Data;

@Data
public class CountBasedPredicateConfiguration {

    private Integer minimum;
    private Integer maximum;

    @Override
    public String toString() {
        if (minimum != null && maximum != null) {
            return "min=" + minimum + ", max=" + maximum;
        }
        if (minimum != null) {
            return "min=" + minimum;
        }
        if (maximum != null) {
            return "max=" + maximum;
        }
        return "";
    }
}
