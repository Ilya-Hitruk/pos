package com.pos.system.validator;

public interface Validator<T> {
    ValidationResult validate(T obj);
}
