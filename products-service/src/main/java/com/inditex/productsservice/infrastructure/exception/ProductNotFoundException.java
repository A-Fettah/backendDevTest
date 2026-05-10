package com.inditex.productsservice.infrastructure.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a product or its similar IDs are not found in the external system.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(String message) {
        super(message);
    }

    public static ProductNotFoundException forId(String productId) {
        return new ProductNotFoundException(String.format("Product with ID %s not found in external system", productId));
    }
}