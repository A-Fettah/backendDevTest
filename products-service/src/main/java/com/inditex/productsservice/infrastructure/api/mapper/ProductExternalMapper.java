package com.inditex.productsservice.infrastructure.api.mapper;

import com.inditex.productsservice.domain.model.Product;
import com.inditex.productsservice.infrastructure.api.dto.ProductExternalDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring") 
public interface ProductExternalMapper {
    Product toDomain(ProductExternalDto dto);
}