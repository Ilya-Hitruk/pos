package com.pos.system.mapper;

public interface Mapper<F, T> {
    T mapFrom(F obj);
}
