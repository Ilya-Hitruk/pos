package com.pos.system;

import lombok.experimental.UtilityClass;
import org.assertj.db.type.Source;

import static com.pos.system.util.ApplicationPropertiesUtil.*;

@UtilityClass
public class AssertjDbSourceProvider {
    private static final String URL_KEY = "db.url";
    private static final String USERNAME_KEY = "db.username";
    private static final String PASSWORD_KEY = "db.password";

    public static Source getSource() {
        return new Source(
                get(URL_KEY),
                get(USERNAME_KEY),
                get(PASSWORD_KEY));
    }
}
