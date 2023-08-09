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

import org.springframework.http.*;
import org.springframework.hateoas.*;
import org.springframework.hateoas.mediatype.problem.*;

import java.util.List;
import java.util.stream.*;

@RestController
public class CustomerOrderController {

  private final OrderRepository orderRepository;
  private final OrderModelAssembler assembler;

  CustomerOrderController(OrderRepository orderRepository, OrderModelAssembler assembler) {

    this.orderRepository = orderRepository;
    this.assembler = assembler;
  }

  @GetMapping("/orders")
  public CollectionModel<EntityModel<CustomerOrder>> all() {

    List<EntityModel<CustomerOrder>> orders = orderRepository.findAll().stream() //
        .map(assembler::toModel) //
        .collect(Collectors.toList());

    return CollectionModel.of(orders, //
        linkTo(methodOn(CustomerOrderController.class).all()).withSelfRel());
  }

  @GetMapping("/orders/{id}")
  public EntityModel<CustomerOrder> one(@PathVariable Long id) {

    CustomerOrder order = orderRepository.findById(id) //
        .orElseThrow(() -> new OrderNotFoundException(id));

    return assembler.toModel(order);
  }

  @PostMapping("/orders")
  ResponseEntity<EntityModel<CustomerOrder>> newOrder(@RequestBody CustomerOrder order) {

    order.setStatus(Status.IN_PROGRESS);
    CustomerOrder newOrder = orderRepository.save(order);

    return ResponseEntity //
        .created(linkTo(methodOn(CustomerOrderController.class).one(newOrder.getId())).toUri()) //
        .body(assembler.toModel(newOrder));
  }

  @DeleteMapping("/orders/{id}/cancel")
  public ResponseEntity<?> cancel(@PathVariable Long id) {

    CustomerOrder order = orderRepository.findById(id) //
        .orElseThrow(() -> new OrderNotFoundException(id));

    if (order.getStatus() == Status.IN_PROGRESS) {
      order.setStatus(Status.CANCELLED);
      return ResponseEntity.ok(assembler.toModel(orderRepository.save(order)));
    }

    return ResponseEntity //
        .status(HttpStatus.METHOD_NOT_ALLOWED) //
        .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE) //
        .body(Problem.create() //
            .withTitle("Method not allowed") //
            .withDetail("You can't cancel an order that is in the " + order.getStatus() + " status"));
  }

  @PutMapping("/orders/{id}/complete")
  public ResponseEntity<?> complete(@PathVariable Long id) {

    CustomerOrder order = orderRepository.findById(id) //
        .orElseThrow(() -> new OrderNotFoundException(id));

    if (order.getStatus() == Status.IN_PROGRESS) {
      order.setStatus(Status.COMPLETED);
      return ResponseEntity.ok(assembler.toModel(orderRepository.save(order)));
    }

    return ResponseEntity //
        .status(HttpStatus.METHOD_NOT_ALLOWED) //
        .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE) //
        .body(Problem.create() //
            .withTitle("Method not allowed") //
            .withDetail("You can't complete an order that is in the " + order.getStatus() + " status"));
  }
}