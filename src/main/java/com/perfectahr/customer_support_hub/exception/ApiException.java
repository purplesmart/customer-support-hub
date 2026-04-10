package com.perfectahr.customer_support_hub.exception;

public class ApiException extends RuntimeException {

    public ApiException(String message) {
        super(message);
    }
}