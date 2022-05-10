package com.ossprj.transmission.task;

import com.ossprj.transmission.task.model.component.DynamicComponentConfiguration;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;

@Component
@ConfigurationProperties("reseed-torrent")
@Data
@Validated
public class ReseedTorrentTaskConfiguration {

    @NotNull
    private String endpoint;

    @NotNull
    private Integer maxActiveTorrents;

    private Boolean dryRun = Boolean.FALSE;

    private List<DynamicComponentConfiguration> predicates = Collections.EMPTY_LIST;

    private List<DynamicComponentConfiguration> comparators = Collections.EMPTY_LIST;


}
