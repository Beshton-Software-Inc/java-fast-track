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

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductRepository productRepository;
    private final ProductModelAssembler productModelAssembler;

    @Autowired
    public ProductController(ProductRepository productRepository, ProductModelAssembler productModelAssembler) {
        this.productRepository = productRepository;
        this.productModelAssembler = productModelAssembler;
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
}
