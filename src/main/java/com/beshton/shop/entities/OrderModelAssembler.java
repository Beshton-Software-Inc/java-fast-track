package com.beshton.shop.entities;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.beshton.shop.exceptions.*;
import com.beshton.shop.entities.*;
import com.beshton.shop.controllers.*;
import com.beshton.shop.advices.*;
import com.beshton.shop.repos.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class OrderModelAssembler implements RepresentationModelAssembler<CustomerOrder, EntityModel<CustomerOrder>> {

    @Override
    public EntityModel<CustomerOrder> toModel(CustomerOrder order) {

        // Unconditional links to single-item resource and aggregate root

        EntityModel<CustomerOrder> orderModel = EntityModel.of(order,
                linkTo(methodOn(CustomerOrderController.class).one(order.getId())).withSelfRel(),
                linkTo(methodOn(CustomerOrderController.class).all()).withRel("orders"));

        // Conditional links based on state of the order

        if (order.getStatus() == Status.IN_PROGRESS) {
            orderModel.add(linkTo(methodOn(CustomerOrderController.class).cancel(order.getId())).withRel("cancel"));
            orderModel.add(linkTo(methodOn(CustomerOrderController.class).complete(order.getId())).withRel("complete"));
        }

        return orderModel;
    }
}