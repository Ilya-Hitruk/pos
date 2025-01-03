package com.pos.system.exception;

public class ExistingCategoryException extends RuntimeException{
    public ExistingCategoryException(String message) {
        super(message);
    }
}
