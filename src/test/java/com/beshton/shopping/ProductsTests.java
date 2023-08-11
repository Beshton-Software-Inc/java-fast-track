package com.beshton.shopping;

import com.beshton.shopping.entity.Product;
import com.beshton.shopping.repository.ProductRepository;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductsTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ProductRepository productRepository;

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setup() {
        productRepository.deleteAll();
    }

    private String productsURL() {
        return "http://localhost:" + port + "/api/products";
    }

    private Product createPhoneProduct() {
        return new Product()
                .setProductName("Phone")
                .setDescription("Cheap phone")
                .setPrice(new BigDecimal("500.00"))
                .setQuantity(500)
                .setCreatedBy("Frank")
                .setUpdatedBy("Frank");
    }

    private Product createDesktopComputerProduct() {
        return new Product()
                .setProductName("Desktop Computer")
                .setDescription("Expensive computer")
                .setPrice(new BigDecimal("1299.02"))
                .setQuantity(2000)
                .setCreatedBy("Jack")
                .setUpdatedBy("Jack");
    }

    private Product createLaptopComputerProduct() {
        return new Product()
                .setProductName("Laptop Computer")
                .setDescription("Very expensive computer")
                .setPrice(new BigDecimal("8275.36"))
                .setQuantity(5290)
                .setCreatedBy("Alice Bob")
                .setUpdatedBy("Alice Bob");
    }

    private Product createInvalidProduct() {
        return new Product()
                .setProductName("Invalid Product")
                .setDescription("Negative price, invalid price decimal, negative quantity")
                .setPrice(new BigDecimal("-12345.6789"))
                .setQuantity(-67)
                .setCreatedBy("creator")
                .setUpdatedBy("creator");
    }

    @Test
    void contextLoads() {
    }

    @Test
    public void testCreateProduct() {
        Product product = createPhoneProduct();

        // Send a HTTP POST request
        ResponseEntity<Product> postResponse = restTemplate.postForEntity(productsURL(), product, Product.class);

        // Assert that the response status is CREATED
        assertEquals(HttpStatus.CREATED, postResponse.getStatusCode());

        // Assert that the returned product matches what was sent
        Product createdProduct = postResponse.getBody();
        assertNotNull(createdProduct);
        assertEquals(product.getProductName(), createdProduct.getProductName());
        assertEquals(product.getDescription(), createdProduct.getDescription());
        assertEquals(product.getPrice(), createdProduct.getPrice());
        assertEquals(product.getQuantity(), createdProduct.getQuantity());
        assertEquals(product.getCreatedBy(), createdProduct.getCreatedBy());
        assertEquals(product.getUpdatedBy(), createdProduct.getUpdatedBy());

        // Assert that the server-generated ID and dates are not null
        assertNotNull(createdProduct.getId());
        assertNotNull(createdProduct.getCreatedAt());
        assertNotNull(createdProduct.getUpdatedAt());
    }

    @Test
    public void testGetProductById() {
        Product product = createPhoneProduct();

        // persist the product directly via product repository
        Product persistedProduct = productRepository.save(product);
        Long id = persistedProduct.getId();

        // Send a HTTP GET request
        String getRequestURL = productsURL() + "/" + id;
        ResponseEntity<Product> getResponse = restTemplate.getForEntity(getRequestURL, Product.class);

        // Assert that the response is as expected
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertEquals(persistedProduct, getResponse.getBody());

        // Try an erroneous id
        String errorURL = productsURL() + "/" + 100;
        ResponseEntity<String> errorGetResponse = restTemplate.getForEntity(errorURL, String.class);
        assertEquals(HttpStatus.NOT_FOUND, errorGetResponse.getStatusCode());
        assertNotNull(errorGetResponse.getBody());
    }

    @Test
    public void testGetAllProducts() {
        // Try on empty database
        ResponseEntity<List<Product>> emptyGetResponse = restTemplate.exchange(
                productsURL(), HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
        assertEquals(HttpStatus.OK, emptyGetResponse.getStatusCode());
        assertEquals(Collections.emptyList(), emptyGetResponse.getBody());

        // persist a list of products
        productRepository.save(createPhoneProduct());
        productRepository.save(createDesktopComputerProduct());
        productRepository.save(createLaptopComputerProduct());
        List<Product> allPersistedProducts = productRepository.findAll();

        // Send a HTTP GET request
        ResponseEntity<List<Product>> getResponse = restTemplate.exchange(
                productsURL(), HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertEquals(allPersistedProducts, getResponse.getBody());
    }

    @Test
    public void testSearchProducts() {
        // url to search for computer
        String query = "computer";
        String queryURL = productsURL() + "/search?query=" + query;

        // Try on empty database
        ResponseEntity<List<Product>> emptyGetResponse = restTemplate.exchange(
                queryURL, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
        assertEquals(HttpStatus.OK, emptyGetResponse.getStatusCode());
        assertEquals(Collections.emptyList(), emptyGetResponse.getBody());

        // persist a list of products
        productRepository.save(createPhoneProduct());
        productRepository.save(createDesktopComputerProduct());
        productRepository.save(createLaptopComputerProduct());

        // first, validate query result of repository
        List<Product> queryResult = productRepository.findByProductNameContainingIgnoreCase(query);
        assertNotNull(queryResult);
        assertEquals(2, queryResult.size());
        assertTrue(queryResult.get(0).getProductName().toLowerCase().contains(query));
        assertTrue(queryResult.get(1).getProductName().toLowerCase().contains(query));
        assertNotEquals(queryResult.get(0).getId(), queryResult.get(1).getId());

        // search for computer on populated db
        ResponseEntity<List<Product>> getResponse = restTemplate.exchange(
                queryURL, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertEquals(queryResult, getResponse.getBody());
    }

    @Test
    public void testUpdateProduct() {
        Product desktopComputer = productRepository.save(createDesktopComputerProduct());
        Product laptopComputer = createLaptopComputerProduct();
        HttpEntity<Product> requestEntity = new HttpEntity<>(laptopComputer, new HttpHeaders());
        String requestURL = productsURL() + "/" + desktopComputer.getId();
        ResponseEntity<Product> putResponse = restTemplate.exchange(
                requestURL, HttpMethod.PUT, requestEntity, Product.class
        );
        // response code should be OK
        assertEquals(HttpStatus.OK, putResponse.getStatusCode());

        // response body should be non-null product
        Product updatedProduct = putResponse.getBody();
        assertNotNull(updatedProduct);

        // id, created date, and created by should not change from the old product, updated date should change
        assertEquals(desktopComputer.getId(), updatedProduct.getId());
        assertEquals(desktopComputer.getCreatedAt(), updatedProduct.getCreatedAt());
        assertEquals(desktopComputer.getCreatedBy(), updatedProduct.getCreatedBy());
        assertNotEquals(desktopComputer.getUpdatedAt(), updatedProduct.getUpdatedAt());

        // other attributes should equal to the newly created product
        assertEquals(laptopComputer.getProductName(), updatedProduct.getProductName());
        assertEquals(laptopComputer.getDescription(), updatedProduct.getDescription());
        assertEquals(laptopComputer.getPrice(), updatedProduct.getPrice());
        assertEquals(laptopComputer.getQuantity(), updatedProduct.getQuantity());
        assertEquals(laptopComputer.getUpdatedBy(), updatedProduct.getUpdatedBy());

        // Try an erroneous id
        String requestUpdateNotFoundProduct = productsURL() + "/" + (desktopComputer.getId() + 1);
        ResponseEntity<String> errorPutResponse = restTemplate.exchange(
                requestUpdateNotFoundProduct, HttpMethod.PUT, requestEntity, String.class
        );
        assertEquals(HttpStatus.NOT_FOUND, errorPutResponse.getStatusCode());
        assertNotNull(errorPutResponse.getBody());
    }

    @Test
    public void testDeleteProduct() {
        Product phoneProuct = productRepository.save(createPhoneProduct());

        // delete an existing product
        String requestURL = productsURL() + "/" + phoneProuct.getId();
        ResponseEntity<Map<String, Boolean>> deleteResponse = restTemplate.exchange(
                requestURL, HttpMethod.DELETE, null, new ParameterizedTypeReference<>() {});
        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());
        assertNotNull(deleteResponse.getBody());
        assertEquals(Collections.singletonMap("deleted", Boolean.TRUE), deleteResponse.getBody());
        assertFalse(productRepository.existsById(phoneProuct.getId()));

        // the deleted product should not be found
        ResponseEntity<String> deleteNotFoundResponse = restTemplate.exchange(
                requestURL, HttpMethod.DELETE, null, String.class
        );
        assertEquals(HttpStatus.NOT_FOUND, deleteNotFoundResponse.getStatusCode());
        assertNotNull(deleteNotFoundResponse.getBody());
    }

    @Test
    public void testBadRequest() {
        // post invalid product
        Product invalidProduct = createInvalidProduct();
        ResponseEntity<String> postResponse = restTemplate.postForEntity(productsURL(), invalidProduct, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, postResponse.getStatusCode());
        assertNotNull(postResponse.getBody());

        // put invalid product
        Product persisted = productRepository.save(createPhoneProduct());
        String putRequestURL = productsURL() + "/" + persisted.getId();
        HttpEntity<Product> putRequestEntity = new HttpEntity<>(invalidProduct, new HttpHeaders());
        ResponseEntity<String> putResponse = restTemplate.exchange(
                putRequestURL, HttpMethod.PUT, putRequestEntity, String.class
        );
        assertEquals(HttpStatus.BAD_REQUEST, putResponse.getStatusCode());
        assertNotNull(putResponse.getBody());
    }
}
