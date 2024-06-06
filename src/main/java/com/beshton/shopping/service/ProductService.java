package com.beshton.shopping.service;

import com.beshton.shopping.entity.Product;
import com.beshton.shopping.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        Optional<Product> product = productRepository.findById(id);
        return product.orElse(null);
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public File generateProductFile(Product product) throws IOException {
        File file = File.createTempFile("product", ".txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("ID: " + product.getId() + "\n");
            writer.write("Product Name: " + product.getProductName() + "\n");
            writer.write("Description: " + product.getDescription() + "\n");
            writer.write("Price: " + product.getPrice() + "\n");
            writer.write("Quantity: " + product.getQuantity() + "\n");
            writer.write("Created At: " + product.getCreatedAt() + "\n");
            writer.write("Updated At: " + product.getUpdatedAt() + "\n");
            writer.write("Created By: " + product.getCreatedBy() + "\n");
            writer.write("Updated By: " + product.getUpdatedBy() + "\n");
        }
        return file;
    }
    public void deleteProductById(Long id) {
        productRepository.deleteById(id);
    }
}
