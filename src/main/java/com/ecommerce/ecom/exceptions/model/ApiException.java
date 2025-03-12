package com.ecommerce.ecom.exceptions.model;

import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@NoArgsConstructor
public class ApiException extends RuntimeException {
    private String message;
    private HttpStatus statusCode;

    public ApiException(String message, HttpStatus statusCode) {
        super(message);
        this.message = message;
        this.statusCode = statusCode;
    }

    public ApiException(String message) {
        super(message);
    }
}
