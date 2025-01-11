package com.poolviz.pool_viz.repository.generic;

import java.util.List;
import java.util.UUID;

public interface GenericRepository<T> {

    List<T> getAll();
    T getById(UUID id);
}
