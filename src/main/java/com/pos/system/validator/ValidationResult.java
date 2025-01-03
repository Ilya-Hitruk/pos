package com.pos.system.validator;

import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@EqualsAndHashCode
public class ValidationResult {
    private final List<Error> errors;

    public ValidationResult() {
        errors = new ArrayList<>();
    }

    public void addError(Error error) {
        errors.add(error);
    }

    public boolean isNotValid() {
        return !errors.isEmpty();
    }

    public List<Error> getErrors() {
        return Collections.unmodifiableList(errors);
    }
}
