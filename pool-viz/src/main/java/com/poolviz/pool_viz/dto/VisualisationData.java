package com.poolviz.pool_viz.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public class VisualisationData {

    private Map<UUID, Long> transactionCountByAddress;
    private Long totalTransactionCount;
    private Double totalVolume;
}
