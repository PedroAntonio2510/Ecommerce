package io.github.api.controller;

import io.github.api.domain.Product;
import io.github.api.domain.dto.ProductRequestDTO;
import io.github.api.domain.mapper.ProductMapper;
import io.github.api.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController implements GenericController{

    private final ProductService service;
    private final ProductMapper productMapper;

    @PostMapping
    public ResponseEntity<?> save(@RequestBody @Valid ProductRequestDTO dto){
        Product product = productMapper.toEntity(dto);
        URI uri = headerLocation(product.getId());
        service.saveProduct(product);
        return ResponseEntity.created(uri).build();
    }

    @GetMapping
    public ResponseEntity<List<Product>> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) BigDecimal price
            ){
        List<Product> productList = service.searchProducts(name, price);
        return ResponseEntity.ok(productList);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody ProductRequestDTO dto){
        return service.getProductById(id)
                .map(product -> {
                    Product newProduct = productMapper.toEntity(dto);
                    product.setName(newProduct.getName());
                    product.setPrice(newProduct.getPrice());
                    product.setDescription(newProduct.getDescription());

                    service.saveProduct(product);
                    return ResponseEntity.noContent().build();
                }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id){
        Optional<Product> productOptional = service.getProductById(id);
        service.deleteProduct(productOptional.get());
        return ResponseEntity.noContent().build();
    }



}
