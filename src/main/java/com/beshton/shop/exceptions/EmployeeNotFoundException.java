package com.beshton.shop.exceptions;

import com.beshton.shop.exceptions.*;
import com.beshton.shop.entities.*;
import com.beshton.shop.controllers.*;
import com.beshton.shop.advices.*;
import com.beshton.shop.repos.*;

public class EmployeeNotFoundException extends RuntimeException {

    public EmployeeNotFoundException(Long id) {
        super("Could not find employee " + id);
    }
}