package com.tugba.ecommerce.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST) // HTTP 400 Bad Request döndürür
public class OutOfStockException extends RuntimeException {
    public OutOfStockException(String message) {
        super(message);
    }
}