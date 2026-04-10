package com.perfectahr.customer_support_hub.exception;

import java.time.LocalDateTime;

public record ErrorResponse(int status, String message, LocalDateTime timestamp) {

}