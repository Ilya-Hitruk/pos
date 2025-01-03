package com.pos.system.entity;

import java.util.Optional;
import java.util.stream.Stream;

public enum Measure {
    KG, LTR, UNIT;

    public static Optional<Measure> find(String name) {
        return Stream.of(values())
                .filter(measure -> measure.name().equals(name))
                .findFirst();
    }
}
