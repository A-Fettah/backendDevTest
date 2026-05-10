package com.inditex.productsservice.infrastructure.controller.mapper;

import com.inditex.productsservice.domain.model.Product;
import com.inditex.productsservice.infrastructure.controller.dto.ProductResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductWebMapper {
    ProductResponse toResponse(Product domain);
}