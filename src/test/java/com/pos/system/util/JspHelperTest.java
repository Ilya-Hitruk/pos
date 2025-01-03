package com.pos.system.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JspHelperTest {

    @Test
    void getPath() {
        String expected = "/WEB-INF/jsp/test/path.jsp";
        String actual = JspHelper.getPath("test/path");

        assertThat(actual).isEqualTo(expected);
    }
}