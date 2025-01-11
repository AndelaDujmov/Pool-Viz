package com.poolviz.pool_viz.domain;

import com.poolviz.pool_viz.domain.enums.NodeStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Node {

    private String networkAddress;
    private NodeStatus status;
}
