package com.poolviz.pool_viz.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class Mempool {

    private List<Transaction> transactions = new ArrayList<>(  );
}
