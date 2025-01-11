package com.poolviz.pool_viz.repository.implementations;

import com.poolviz.pool_viz.repository.MempoolRepository;

import java.util.List;
import java.util.UUID;

public class MempoolRepositoryImpl implements MempoolRepository {



    @Override
    public List getAll() {
        return List.of( );
    }

    @Override
    public Object getById(UUID id) {
        return null;
    }
}
