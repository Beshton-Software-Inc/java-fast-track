package com.beshton.payroll.controller;

import com.beshton.payroll.model.Order;
import com.beshton.payroll.model.Status;
import com.beshton.payroll.model.assembler.OrderModelAssembler;
import com.beshton.payroll.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderModelAssembler assembler;

    @Autowired
    public OrderController(OrderService orderService, OrderModelAssembler assembler) {
        this.orderService = orderService;
        this.assembler = assembler;
    }

    @GetMapping
    public CollectionModel<EntityModel<Order>> all() {
        List<EntityModel<Order>> orders = orderService.findAllOrders().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(orders, linkTo(methodOn(OrderController.class).all()).withSelfRel());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Order>> one(@PathVariable Long id) {
        Order order = orderService.findOrderById(id);
        return ResponseEntity.ok(assembler.toModel(order));
    }

    @PostMapping
    public ResponseEntity<EntityModel<Order>> newOrder(@RequestBody Order order) {
        order.setStatus(Status.IN_PROGRESS);
        Order newOrder = orderService.saveOrder(order);
        return ResponseEntity.created(linkTo(methodOn(OrderController.class).one(newOrder.getId())).toUri())
                .body(assembler.toModel(newOrder));
    }

    @DeleteMapping("/{id}/cancel")
    public ResponseEntity<?> cancel(@PathVariable Long id) {
        try {
            Order order = orderService.updateOrderStatus(id, Status.CANCELLED);
            return ResponseEntity.ok(assembler.toModel(order));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                    .body("Order cannot be cancelled as it is not in IN_PROGRESS status.");
        }
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<?> complete(@PathVariable Long id) {
        try {
            Order order = orderService.updateOrderStatus(id, Status.COMPLETED);
            return ResponseEntity.ok(assembler.toModel(order));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                    .body("Order cannot be completed as it is not in IN_PROGRESS status.");
        }
    }
}
