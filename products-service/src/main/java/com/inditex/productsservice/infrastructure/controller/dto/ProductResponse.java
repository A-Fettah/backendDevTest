package com.inditex.productsservice.infrastructure.controller.dto;

import lombok.Data;

@Data
public class ProductResponse {
    private String id;
    private String name;
    private Double price;
    private Boolean availability;
}