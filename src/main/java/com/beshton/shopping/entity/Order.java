package com.beshton.shopping.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.CreatedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "customerName", nullable = false)
    @Size(min = 2, message = "Name should have at least 2 characters")
    private String customerName;
    @Column(name = "productID", nullable = false)
    private String productID;
    @Column(name = "quantity", nullable = false)
    @Min(value = 0, message = "Quantity should not be less than 0")
    private Integer quantity;
    @Column(name = "price", nullable = false)
    @Min(value = 0, message = "Quantity should not be less than 0")
    private BigDecimal price;
    @Column(name = "shippingAddress", nullable = false)
    private String shippingAddress;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    public enum OrderStatus {
        PENDING("Pending"),
        PROCESSING("Processing"),
        SHIPPED("Shipped"),
        DELIVERED("Delivered"),
        CANCELLED("Cancelled");

        // Constructor of new orderStatus
        private final String displayName;

        OrderStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }


    public long getId() {
        return id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public Order setCustomerName(String customerName) {
        this.customerName = customerName;
        return this;
    }


    public String getProductID() {
        return productID;
    }

    public Order setProductID(String productID) {
        this.productID = productID;
        return this;
    }

    public int getQuantity() {
        return quantity;
    }

    public Order setQuantity(Integer quantity) {
        this.quantity = quantity;
        return this;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Order setPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public Order setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
        return this;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public Order setStatus(OrderStatus status) {
        this.status = status;
        return this;
    }

    @Override
public String toString() {
    return "Product{" +
            "id=" + id +
            ", customerName ='" + customerName + '\'' +
            ", productID='" + productID + '\'' +
            ", price=" + price +
            ", quantity=" + quantity +
            ", shippingAddress=" + shippingAddress +
            '}';

}
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order order)) return false;

        if (!Objects.equals(id, order.id)) return false;
        if (!Objects.equals(customerName, order.customerName)) return false;
        if (!Objects.equals(productID, order.productID)) return false;
        if (!Objects.equals(price, order.price)) return false;
        if (!Objects.equals(quantity, order.quantity)) return false;
        if (!Objects.equals(shippingAddress, order.shippingAddress)) return false;
        return Objects.equals(id, order.id);
    }



}