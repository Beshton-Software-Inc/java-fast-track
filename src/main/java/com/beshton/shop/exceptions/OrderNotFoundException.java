package com.beshton.shop.exceptions;

import com.beshton.shop.exceptions.*;
import com.beshton.shop.entities.*;
import com.beshton.shop.controllers.*;
import com.beshton.shop.advices.*;
import com.beshton.shop.repos.*;

public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(Long id) {
        super("Could not find employee " + id);
    }
}