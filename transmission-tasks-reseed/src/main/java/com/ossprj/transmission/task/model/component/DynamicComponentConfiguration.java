package com.ossprj.transmission.task.model.component;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.Map;

@Data
@Validated
public class DynamicComponentConfiguration {

    @NotNull
    private String name;

    private Map<String, Object> config;

}
