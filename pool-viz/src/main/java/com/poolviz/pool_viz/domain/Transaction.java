package com.poolviz.pool_viz.domain;

import com.poolviz.pool_viz.domain.enums.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
public class Transaction extends BaseEntity {

    private String sender;
    private String recipient;
    private BigDecimal amount;
    private Instant timestamp;
    private TransactionStatus status;
}
