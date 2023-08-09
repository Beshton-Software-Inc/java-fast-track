
package com.beshton.shop.controllers;

import com.beshton.shop.advices.*;
import com.beshton.shop.controllers.*;
import com.beshton.shop.entities.*;
import com.beshton.shop.exceptions.*;
import com.beshton.shop.repos.*;
import org.springframework.web.bind.annotation.*;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.*;

@Controller
public class HomeController {

    @RequestMapping(value = "/")
    public String index() {
        return "index";
    }


    @GetMapping("/greeting")
    public String greeting(@RequestParam(name="name", required=false, defaultValue="World") String name) {
        return "greeting";
    }

}