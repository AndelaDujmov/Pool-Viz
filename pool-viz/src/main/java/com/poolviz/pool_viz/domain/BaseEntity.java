package com.poolviz.pool_viz.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class BaseEntity {

    private UUID id;

    private Date createdAt;

    private Date modifiedAt;

    public BaseEntity() {

        this.id = UUID.randomUUID();
    }
}
