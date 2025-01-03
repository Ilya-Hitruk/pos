package com.pos.system.dao;

import java.util.List;
import java.util.Optional;

public interface Dao<K, T> {
    K save(T entity);
    Optional<T> findById(K id);
    List<T> findAll();
    boolean update(T entity);
    boolean delete(K id);
}
