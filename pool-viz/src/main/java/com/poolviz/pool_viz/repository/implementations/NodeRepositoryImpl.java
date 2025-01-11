package com.poolviz.pool_viz.repository.implementations;

import com.poolviz.pool_viz.repository.NodeRepository;

import java.util.List;
import java.util.UUID;

public class NodeRepositoryImpl implements NodeRepository  {

    @Override
    public List getAll() {
        return List.of( );
    }

    @Override
    public Object getById(UUID id) {
        return null;
    }
}
