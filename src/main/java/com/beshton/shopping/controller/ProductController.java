package com.beshton.shopping.controller;

import com.beshton.shopping.entity.Product;
import com.beshton.shopping.exception.ProductNotFoundException;
import com.beshton.shopping.repository.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductRepository productRepository;

    @Autowired
    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // Create
    @PostMapping
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product) {
        Product savedProduct = productRepository.save(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
    }

    // Read
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(
            @PathVariable(value = "id") Long productId) throws ProductNotFoundException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found on :: " + productId));
        return ResponseEntity.ok(product);
    }

    // Read (all)
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productRepository.findAll());
    }

    // Read (search)
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String query) {
        return ResponseEntity.ok(productRepository.findByProductNameContainingIgnoreCase(query));
    }

    // Update
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
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
        return ResponseEntity.ok(updatedProduct);
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
}
