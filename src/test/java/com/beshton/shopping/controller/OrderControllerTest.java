package com.beshton.shopping.controller;

import com.beshton.shopping.entity.Order;
import com.beshton.shopping.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrderControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private OrderRepository orderRepository;

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setup() {
        orderRepository.deleteAll();
    }

    private String ordersURL() {
        return "http://localhost:" + port + "/api/orders";
    }
    private Order createTestOrder() {
        return new Order()
                .setCustomerName("Jeff")
                .setProductID("X2333")
                .setQuantity(500)
                .setPrice(new BigDecimal("123.44"))
                .setShippingAddress("123 Where St");
    }

    private Order createAnotherTestOrder() {
        return new Order()
                .setCustomerName("Jackson")
                .setProductID("Y666")
                .setQuantity(700)
                .setPrice(new BigDecimal("566.77"))
                .setShippingAddress("258 What Ave");
    }

    private Order createInvalidOrder() {
        return new Order()
                .setCustomerName("Wrong")
                .setProductID("DELETE_ALL")
                .setQuantity(-233)
                .setPrice(new BigDecimal("-12345.6789"))
                .setShippingAddress("TRAP");
    }

//    Order RECORD_1 = new Order("Gem", "X233", 6);
//    Order RECORD_2 = new Order("Mint", "X2333", 7);
//    Order RECORD_3 = new Order("Zhemin", "X23333", 8);

    @Test
    void contextLoads() {
    }

    @Test
    public void testCreateOrder() {
        Order order = createTestOrder();

        // Send a HTTP POST request
        ResponseEntity<Order> postResponse = restTemplate.postForEntity(ordersURL(), order, Order.class);

        // Assert that the response status is CREATED
        assertEquals(HttpStatus.CREATED, postResponse.getStatusCode());

        // Assert that the returned order matches what was sent
        Order createdOrder = postResponse.getBody();
        assertNotNull(createdOrder);
        assertEquals(order.getCustomerName(), createdOrder.getCustomerName());
        assertEquals(order.getProductID(), createdOrder.getProductID());
        assertEquals(order.getPrice(), createdOrder.getPrice());
        assertEquals(order.getQuantity(), createdOrder.getQuantity());
        assertEquals(order.getShippingAddress(), createdOrder.getShippingAddress());

        // Assert that the server-generated ID and dates are not null
        assertNotNull(createdOrder.getCustomerName());
    }

    @Test
    public void testGetOrderById() {
        Order order = createTestOrder();

        // persist the order directly via order repository
        Order persistedOrder = orderRepository.save(order);
        long id = persistedOrder.getId();

        // Send a HTTP GET request
        String getRequestURL = ordersURL() + "/" + id;
        ResponseEntity<Order> getResponse = restTemplate.getForEntity(getRequestURL, Order.class);

        // Assert that the response is as expected
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertEquals(persistedOrder, getResponse.getBody());

        // Try an erroneous id
        String errorURL = ordersURL() + "/" + 100;
        ResponseEntity<String> errorGetResponse = restTemplate.getForEntity(errorURL, String.class);
        assertEquals(HttpStatus.NOT_FOUND, errorGetResponse.getStatusCode());
        assertNotNull(errorGetResponse.getBody());
    }

    @Test
    public void testGetAllOrders() {
        // Try on empty database
        ResponseEntity<List<Order>> emptyGetResponse = restTemplate.exchange(
                ordersURL(), HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
        assertEquals(HttpStatus.OK, emptyGetResponse.getStatusCode());
        assertEquals(Collections.emptyList(), emptyGetResponse.getBody());

        // persist a list of order
        orderRepository.save(createTestOrder());
        orderRepository.save(createAnotherTestOrder());
        List<Order> allPersistedOrders = orderRepository.findAll();

        // Send a HTTP GET request
        ResponseEntity<List<Order>> getResponse = restTemplate.exchange(
                ordersURL(), HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertEquals(allPersistedOrders, getResponse.getBody());
    }

    @Test
    public void testUpdateOrder() {
        Order test_one = orderRepository.save(createTestOrder());
        Order test_another = createAnotherTestOrder();
        HttpEntity<Order> requestEntity = new HttpEntity<>(test_another, new HttpHeaders());
        String requestURL = ordersURL() + "/" + test_one.getId();
        ResponseEntity<Order> putResponse = restTemplate.exchange(
                requestURL, HttpMethod.PUT, requestEntity, Order.class
        );
        // response code should be OK
        assertEquals(HttpStatus.OK, putResponse.getStatusCode());

        // response body should be non-null order
        Order updatedOrder = putResponse.getBody();
        assertNotNull(updatedOrder);

        // id should not change from the old order
        assertEquals(test_one.getId(), updatedOrder.getId());

        // other attributes should equal to the newly created order
        assertEquals(test_another.getProductID(), updatedOrder.getProductID());
        assertEquals(test_another.getPrice(), updatedOrder.getPrice());
        assertEquals(test_another.getQuantity(), updatedOrder.getQuantity());
        assertEquals(test_another.getShippingAddress(), updatedOrder.getShippingAddress());

        // Try an erroneous id
        String requestUpdateNotFoundProduct = ordersURL() + "/" + (test_one.getId() + 1);
        ResponseEntity<String> errorPutResponse = restTemplate.exchange(
                requestUpdateNotFoundProduct, HttpMethod.PUT, requestEntity, String.class
        );
        assertEquals(HttpStatus.NOT_FOUND, errorPutResponse.getStatusCode());
        assertNotNull(errorPutResponse.getBody());
    }

    @Test
    public void testDeleteOrder() {
        Order test_one = orderRepository.save(createTestOrder());

        // delete an existing order
        String requestURL = ordersURL() + "/" + test_one.getId();
        ResponseEntity<Map<String, Boolean>> deleteResponse = restTemplate.exchange(
                requestURL, HttpMethod.DELETE, null, new ParameterizedTypeReference<>() {});
        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());
        assertNotNull(deleteResponse.getBody());
        assertEquals(Collections.singletonMap("deleted", Boolean.TRUE), deleteResponse.getBody());
        assertFalse(orderRepository.existsById(test_one.getId()));

        // the deleted order should not be found
        ResponseEntity<String> deleteNotFoundResponse = restTemplate.exchange(
                requestURL, HttpMethod.DELETE, null, String.class
        );
        assertEquals(HttpStatus.NOT_FOUND, deleteNotFoundResponse.getStatusCode());
        assertNotNull(deleteNotFoundResponse.getBody());
    }

    @Test
    public void testBadRequest() {
        // post invalid Order
        Order invalidOrder = createInvalidOrder();
        ResponseEntity<String> postResponse = restTemplate.postForEntity(ordersURL(), invalidOrder, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, postResponse.getStatusCode());
        assertNotNull(postResponse.getBody());

        // put invalid order
        Order persisted = orderRepository.save(createTestOrder());
        String putRequestURL = ordersURL() + "/" + persisted.getId();
        HttpEntity<Order> putRequestEntity = new HttpEntity<>(invalidOrder, new HttpHeaders());
        ResponseEntity<String> putResponse = restTemplate.exchange(
                putRequestURL, HttpMethod.PUT, putRequestEntity, String.class
        );
        assertEquals(HttpStatus.BAD_REQUEST, putResponse.getStatusCode());
        assertNotNull(putResponse.getBody());
    }
}
