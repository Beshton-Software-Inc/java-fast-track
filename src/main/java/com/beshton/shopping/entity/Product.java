package com.beshton.shopping.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@Entity
@Table(name = "products")
@EntityListeners(AuditingEntityListener.class)
public class Product {
    private static final Product EMPTY_PRODUCT = new Product();

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "price", nullable = false)
    @Digits(integer = 10, fraction = 2, message = "price must has the form 1234567890.12")
    @DecimalMin(value = "0.00", message = "price must be greater than or equal to 0.00")
    private BigDecimal price;

    @Column(name = "quantity", nullable = false)
    @Min(value = 0, message = "quantity must be greater than or equal to 0")
    private Integer quantity;

    @Column(name = "created_at", nullable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Column(name = "created_by", nullable = false)
    @CreatedBy
    private String createdBy;

    @Column(name = "updated_by", nullable = false)
    @LastModifiedBy
    private String updatedBy;

    public String getProductName() {
        return productName;
    }

    public Long getId() {
        return id;
    }

    public Product setProductName(String productName) {
        this.productName = productName;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Product setDescription(String description) {
        this.description = description;
        return this;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Product setPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Product setQuantity(Integer quantity) {
        this.quantity = quantity;
        return this;
    }

    public LocalDateTime getCreatedAt() {
        if (createdAt == null) {
            return null;
        }
        return createdAt.truncatedTo(ChronoUnit.MILLIS);
    }

    public Product setCreatedAt(LocalDateTime createdAt) {
        if (createdAt != null) {
            createdAt = createdAt.truncatedTo(ChronoUnit.MILLIS);
        }
        this.createdAt = createdAt;
        return this;
    }

    public LocalDateTime getUpdatedAt() {
        if (updatedAt == null) {
            return null;
        }
        return updatedAt.truncatedTo(ChronoUnit.MILLIS);
    }

    public Product setUpdatedAt(LocalDateTime updatedAt) {
        if (updatedAt != null) {
            updatedAt = updatedAt.truncatedTo(ChronoUnit.MILLIS);
        }
        this.updatedAt = updatedAt;
        return this;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public Product setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public Product setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
        return this;
    }

    public static Product emptyProduct() {
        return EMPTY_PRODUCT;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", productName='" + productName + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", createdBy='" + createdBy + '\'' +
                ", updatedBy='" + updatedBy + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product product)) return false;
        return Objects.equals(getId(), product.getId())
                && Objects.equals(getProductName(), product.getProductName())
                && Objects.equals(getDescription(), product.getDescription())
                && Objects.equals(getPrice(), product.getPrice())
                && Objects.equals(getQuantity(), product.getQuantity())
                && Objects.equals(getCreatedAt(), product.getCreatedAt())
                && Objects.equals(getUpdatedAt(), product.getUpdatedAt())
                && Objects.equals(getCreatedBy(), product.getCreatedBy())
                && Objects.equals(getUpdatedBy(), product.getUpdatedBy());
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (productName != null ? productName.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (price != null ? price.hashCode() : 0);
        result = 31 * result + (quantity != null ? quantity.hashCode() : 0);
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
        result = 31 * result + (updatedAt != null ? updatedAt.hashCode() : 0);
        result = 31 * result + (createdBy != null ? createdBy.hashCode() : 0);
        result = 31 * result + (updatedBy != null ? updatedBy.hashCode() : 0);
        return result;
    }
}
