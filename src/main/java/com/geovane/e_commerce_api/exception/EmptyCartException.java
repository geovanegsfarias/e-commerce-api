package com.geovane.e_commerce_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class EmptyCartException extends RuntimeException {

    public EmptyCartException(String message) {
        super(message);
    }
}