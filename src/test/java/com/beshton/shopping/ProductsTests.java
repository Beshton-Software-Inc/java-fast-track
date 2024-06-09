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
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
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
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Product> requestEntity = new HttpEntity<>(product, headers);
        ResponseEntity<EntityModel<Product>> postResponse = restTemplate.exchange(
                productsURL(), HttpMethod.POST, requestEntity, new ParameterizedTypeReference<>() {});

        // Assert that the response status is CREATED
        assertEquals(HttpStatus.CREATED, postResponse.getStatusCode());

        // Assert that the returned resource matches what was sent
        EntityModel<Product> returnedResource = postResponse.getBody();
        assertNotNull(returnedResource);
        assertNotNull(returnedResource.getContent());
        assertEquals(product.getProductName(), returnedResource.getContent().getProductName());
        assertEquals(product.getDescription(), returnedResource.getContent().getDescription());
        assertEquals(product.getPrice(), returnedResource.getContent().getPrice());
        assertEquals(product.getQuantity(), returnedResource.getContent().getQuantity());
        assertEquals(product.getCreatedBy(), returnedResource.getContent().getCreatedBy());
        assertEquals(product.getUpdatedBy(), returnedResource.getContent().getUpdatedBy());

        // Assert that the server-generated ID and dates are not null
        assertNotNull(returnedResource.getContent().getId());
        assertNotNull(returnedResource.getContent().getCreatedAt());
        assertNotNull(returnedResource.getContent().getUpdatedAt());

        // Assert that the response includes links
        assertNotNull(returnedResource.getLinks());
        assertFalse(returnedResource.getLinks().isEmpty());
    }

    @Test
    public void testGetProductById() {
        Product product = createPhoneProduct();

        // persist the product directly via product repository
        Product persistedProduct = productRepository.save(product);
        Long id = persistedProduct.getId();

        // Send a HTTP GET request
        String getRequestURL = productsURL() + "/" + id;
        ResponseEntity<EntityModel<Product>> getResponse = restTemplate.exchange(
                getRequestURL, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

        // Assert that the response is as expected
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(getResponse.getBody());
        assertNotNull(getResponse.getBody());
        assertNotNull(getResponse.getBody().getContent());
        assertEquals(persistedProduct, getResponse.getBody().getContent());
        assertNotNull(getResponse.getBody().getLinks());
        assertFalse(getResponse.getBody().getLinks().isEmpty());

        // Try an erroneous id
        String errorURL = productsURL() + "/" + 100;
        ResponseEntity<String> errorGetResponse = restTemplate.getForEntity(errorURL, String.class);
        assertEquals(HttpStatus.NOT_FOUND, errorGetResponse.getStatusCode());
        assertNotNull(errorGetResponse.getBody());
    }

    @Test
    public void testGetAllProducts() {
        // Try on empty database
        ResponseEntity<CollectionModel<EntityModel<Product>>> emptyGetResponse = restTemplate.exchange(
                productsURL(), HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
        assertEquals(HttpStatus.OK, emptyGetResponse.getStatusCode());
        assertNotNull(emptyGetResponse.getBody());
        assertTrue(emptyGetResponse.getBody().getContent().isEmpty());
        assertNotNull(emptyGetResponse.getBody().getLinks());
        assertFalse(emptyGetResponse.getBody().getLinks().isEmpty());

        // persist a list of products
        productRepository.save(createPhoneProduct());
        productRepository.save(createDesktopComputerProduct());
        productRepository.save(createLaptopComputerProduct());
        List<Product> allPersistedProducts = productRepository.findAll();

        // Send a HTTP GET request
        ResponseEntity<CollectionModel<EntityModel<Product>>> getResponse = restTemplate.exchange(
                productsURL(), HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(getResponse.getBody());
        assertEquals(allPersistedProducts.size(), getResponse.getBody().getContent().size());
        assertNotNull(getResponse.getBody().getLinks());
        assertFalse(getResponse.getBody().getLinks().isEmpty());
    }

    @Test
    public void testSearchProducts() {
        // url to search for computer
        String query = "computer";
        String queryURL = productsURL() + "/search?query=" + query;

        // Try on empty database
        ResponseEntity<CollectionModel<EntityModel<Product>>> emptyGetResponse = restTemplate.exchange(
                queryURL, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
        assertEquals(HttpStatus.OK, emptyGetResponse.getStatusCode());
        assertNotNull(emptyGetResponse.getBody());
        assertNotNull(emptyGetResponse.getBody().getContent());
        assertTrue(emptyGetResponse.getBody().getContent().isEmpty());
        assertNotNull(emptyGetResponse.getBody().getLinks());
        assertFalse(emptyGetResponse.getBody().getLinks().isEmpty());

        // persist a list of products
        productRepository.save(createPhoneProduct());
        productRepository.save(createDesktopComputerProduct());
        productRepository.save(createLaptopComputerProduct());

        // validate query result of repository
        List<Product> queryResult = productRepository.findByProductNameContainingIgnoreCase(query);
        assertNotNull(queryResult);
        assertEquals(2, queryResult.size());
        assertTrue(queryResult.get(0).getProductName().toLowerCase().contains(query));
        assertTrue(queryResult.get(1).getProductName().toLowerCase().contains(query));
        assertNotEquals(queryResult.get(0).getId(), queryResult.get(1).getId());

        // search for computer on populated db
        ResponseEntity<CollectionModel<EntityModel<Product>>> getResponse = restTemplate.exchange(
                queryURL, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(getResponse.getBody());
        assertNotNull(getResponse.getBody().getContent());
        assertEquals(queryResult.size(), getResponse.getBody().getContent().size());
        assertNotNull(getResponse.getBody().getLinks());
        assertFalse(getResponse.getBody().getLinks().isEmpty());
    }

    @Test
    public void testUpdateProduct() {
        Product desktopComputer = productRepository.save(createDesktopComputerProduct());
        Product laptopComputer = createLaptopComputerProduct();
        HttpEntity<Product> requestEntity = new HttpEntity<>(laptopComputer, new HttpHeaders());
        String requestURL = productsURL() + "/" + desktopComputer.getId();
        ResponseEntity<EntityModel<Product>> putResponse = restTemplate.exchange(
                requestURL, HttpMethod.PUT, requestEntity, new ParameterizedTypeReference<>() {});
        // response code should be OK
        assertEquals(HttpStatus.OK, putResponse.getStatusCode());

        // response body should be non-null resource containing a product and links
        assertNotNull(putResponse.getBody());
        assertNotNull(putResponse.getBody().getContent());
        assertNotNull(putResponse.getBody().getLinks());
        assertFalse(putResponse.getBody().getLinks().isEmpty());

        // id, created date, and created by should not change from the old product, updated date should change
        assertEquals(desktopComputer.getId(), putResponse.getBody().getContent().getId());
        assertEquals(desktopComputer.getCreatedAt(), putResponse.getBody().getContent().getCreatedAt());
        assertEquals(desktopComputer.getCreatedBy(), putResponse.getBody().getContent().getCreatedBy());
        assertNotEquals(desktopComputer.getUpdatedAt(), putResponse.getBody().getContent().getUpdatedAt());

        // other attributes should equal to the newly created product
        assertEquals(laptopComputer.getProductName(), putResponse.getBody().getContent().getProductName());
        assertEquals(laptopComputer.getDescription(), putResponse.getBody().getContent().getDescription());
        assertEquals(laptopComputer.getPrice(), putResponse.getBody().getContent().getPrice());
        assertEquals(laptopComputer.getQuantity(), putResponse.getBody().getContent().getQuantity());
        assertEquals(laptopComputer.getUpdatedBy(), putResponse.getBody().getContent().getUpdatedBy());

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
        Product phoneProduct = productRepository.save(createPhoneProduct());

        // delete an existing product
        String requestURL = productsURL() + "/" + phoneProduct.getId();
        ResponseEntity<Map<String, Boolean>> deleteResponse = restTemplate.exchange(
                requestURL, HttpMethod.DELETE, null, new ParameterizedTypeReference<>() {});
        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());
        assertNotNull(deleteResponse.getBody());
        assertEquals(Collections.singletonMap("deleted", Boolean.TRUE), deleteResponse.getBody());
        assertFalse(productRepository.existsById(phoneProduct.getId()));

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
