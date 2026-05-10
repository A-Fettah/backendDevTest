package com.inditex.productsservice.infrastructure.controller;

import com.inditex.productsservice.application.service.SimilarProductService;
import com.inditex.productsservice.infrastructure.controller.dto.ProductResponse;
import com.inditex.productsservice.infrastructure.controller.mapper.ProductWebMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import java.util.List;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final SimilarProductService service;
    private final ProductWebMapper mapper;

    @GetMapping("/{productId}/similar")
    public Mono<ResponseEntity<List<ProductResponse>>> getSimilarProducts(@PathVariable String productId) {
        return service.getSimilarProducts(productId)
                .map(mapper::toResponse)
                .collectList() 
                .map(list -> ResponseEntity.ok(list)) 
                .defaultIfEmpty(ResponseEntity.notFound().build()); 
    }
}