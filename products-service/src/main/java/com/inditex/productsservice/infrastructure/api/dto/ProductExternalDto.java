package com.inditex.productsservice.infrastructure.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductExternalDto {
    private String id;
    private String name;
    private Double price;
    private Boolean availability;
}