package com.beshton.shopping.exception;

public class ProductNotFoundException extends ResourceNotFoundException {

    public ProductNotFoundException(String message) {
        super(message);
    }
}
