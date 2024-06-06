package com.beshton.shopping.controller;

import com.beshton.shopping.entity.Product;
import com.beshton.shopping.exception.ProductNotFoundException;
import com.beshton.shopping.mapper.ProductModelAssembler;
import com.beshton.shopping.repository.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.beshton.shopping.service.S3Service;
import com.beshton.shopping.service.ProductService;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductRepository productRepository;
    private final ProductModelAssembler productModelAssembler;
    private final ProductService productService;
    private final S3Service s3Service;

    public ProductController(ProductRepository productRepository,
                             ProductModelAssembler productModelAssembler,
                             ProductService productService,
                             S3Service s3Service) {
        this.productRepository = productRepository;
        this.productModelAssembler = productModelAssembler;
        this.productService = productService;
        this.s3Service = s3Service;
    }
    // Create
    @PostMapping
    public ResponseEntity<EntityModel<Product>> createProduct(@Valid @RequestBody Product product) {
        Product savedProduct = productRepository.save(product);
        EntityModel<Product> productModel = productModelAssembler.toModel(savedProduct);
        return ResponseEntity.status(HttpStatus.CREATED).body(productModel);
    }

    // Read
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Product>> getProductById(
            @PathVariable(value = "id") Long productId) throws ProductNotFoundException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found on :: " + productId));
        EntityModel<Product> productModel = productModelAssembler.toModel(product);
        return ResponseEntity.ok(productModel);
    }

    // Read (all)
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Product>>> getAllProducts() {
        return ResponseEntity.ok(productModelAssembler.toCollectionModel(productRepository.findAll()));
    }

    // Read (search)
    @GetMapping("/search")
    public ResponseEntity<CollectionModel<EntityModel<Product>>> searchProducts(@RequestParam String query) {
        return ResponseEntity.ok(productModelAssembler.toCollectionModel(
                productRepository.findByProductNameContainingIgnoreCase(query)));
    }

    // Update
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Product>> updateProduct(
            @PathVariable(value = "id") Long productId,
            @Valid @RequestBody Product product) throws ProductNotFoundException {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found on :: " + productId));
        existingProduct
                .setProductName(product.getProductName())
                .setDescription(product.getDescription())
                .setPrice(product.getPrice())
                .setQuantity(product.getQuantity())
                .setUpdatedBy(product.getUpdatedBy())
                .setUpdatedAt(LocalDateTime.now());
        Product updatedProduct = productRepository.save(existingProduct);
        return ResponseEntity.ok(productModelAssembler.toModel(updatedProduct));
    }

    // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Boolean>> deleteProduct(
            @PathVariable(value = "id") Long productId) throws ProductNotFoundException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found on :: " + productId));
        productRepository.delete(product);
        return ResponseEntity.ok(Collections.singletonMap("deleted", Boolean.TRUE));
    }

    // Upload product file to S3
    @PostMapping("/upload")
    public ResponseEntity<String> uploadProductFile(@RequestParam("bucketName") String bucketName,
                                                    @RequestParam("key") String key,
                                                    @RequestParam("productName") String productName,
                                                    @RequestParam("description") String description,
                                                    @RequestParam("price") Double price,
                                                    @RequestParam("quantity") Integer quantity) throws IOException {
        // create Product and save
        Product product = new Product();
        product.setProductName(productName);
        product.setDescription(description);
        product.setPrice(BigDecimal.valueOf(price));
        product.setQuantity(quantity);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        product.setCreatedBy("system");
        product.setUpdatedBy("system");

        Product savedProduct = productRepository.save(product);

        // create Product file
        File productFile = productService.generateProductFile(savedProduct);

        // upload to S3
        s3Service.uploadFile(bucketName, key, productFile);

        return ResponseEntity.ok("Successfully uploaded product to S3.");
    }

    // Delete product file from S3
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<String> deleteProductFile(@PathVariable Long id,
                                                    @RequestParam("bucketName") String bucketName,
                                                    @RequestParam("key") String key) throws ProductNotFoundException {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found on :: " + id));
        // delete from S3
        s3Service.deleteFile(bucketName, key);
        // Delete customer from database
        productService.deleteProductById(id);
        return ResponseEntity.ok("Successfully deleted product from the S3.");
    }
}
