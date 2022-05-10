package com.ossprj.transmission.task.model.comparator;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@Validated
public class CountBasedComparatorConfiguration {

    @NotNull
    private ComparatorSortOrder sortOrder;

}
