package com.pos.system.validator;

import lombok.Value;

import java.util.List;

@Value(staticConstructor = "of")
public class Error {
    String code;
    String message;
}
