package com.pos.system.util;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;


class ApplicationPropertiesUtilTest {

    @ParameterizedTest
    @MethodSource("getPropertyArguments")
    void get(String key, String value) {
        String actualResult = ApplicationPropertiesUtil.get(key);
        assertThat(actualResult).isEqualTo(value);
    }

    static Stream<Arguments> getPropertyArguments() {
        return Stream.of(
                Arguments.of("db.url", "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1"),
                Arguments.of("db.username", "sa"),
                Arguments.of("db.password", ""),
                Arguments.of("db.driver", "org.h2.Driver"),
                Arguments.of("dummy", null)
        );
    }
}