package com.beshton.shopping.mapper;

import com.beshton.shopping.controller.ProductController;
import com.beshton.shopping.entity.Product;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.stream.StreamSupport;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ProductModelAssembler implements RepresentationModelAssembler<Product, EntityModel<Product>> {
    @Override
    @NonNull
    public EntityModel<Product> toModel(@NonNull Product product) {
        long id = product.getId();
        return EntityModel.of(
                product,
                linkToGetProductById(id),
                linkToUpdateProduct(id),
                linkToDeleteProduct(id));
    }

    @Override
    @NonNull
    public CollectionModel<EntityModel<Product>> toCollectionModel(Iterable<? extends Product> products) {
        return CollectionModel.of(
                StreamSupport.stream(products.spliterator(), false).map(this::toModel).toList(),
                linkToGetAllProducts(),
                linkToSearchProducts(),
                linkToCreateProduct());
    }

    private Link linkToCreateProduct() {
        return linkTo(methodOn(ProductController.class).createProduct(Product.emptyProduct())).withRel("create");
    }

    private Link linkToGetProductById(Long id) {
        return linkTo(methodOn(ProductController.class).getProductById(id)).withSelfRel();
    }

    private Link linkToGetAllProducts() {
        return linkTo(methodOn(ProductController.class).getAllProducts()).withRel("products");
    }

    private Link linkToSearchProducts() {
        return linkTo(methodOn(ProductController.class).searchProducts("search_query")).withRel("search");
    }

    private Link linkToUpdateProduct(Long id) {
        return linkTo(methodOn(ProductController.class).updateProduct(id, Product.emptyProduct())).withRel("update");
    }

    private Link linkToDeleteProduct(Long id) {
        return linkTo(methodOn(ProductController.class).deleteProduct(id)).withRel("delete");
    }
}
