package com.example.web_app.rest;

import com.example.web_app.model.ProductDTO;
import com.example.web_app.service.ProductService;
import com.example.web_app.util.ReferencedException;
import com.example.web_app.util.ReferencedWarning;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/products", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductResource {

    private final ProductService productService;

    public ProductResource(final ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        return ResponseEntity.ok(productService.findAll());
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDTO> getProduct(
            @PathVariable(name = "productId") final Integer productId) {
        return ResponseEntity.ok(productService.get(productId));
    }

    @PostMapping
    public ResponseEntity<Integer> createProduct(@RequestBody @Valid final ProductDTO productDTO) {
        final Integer createdProductId = productService.create(productDTO);
        return new ResponseEntity<>(createdProductId, HttpStatus.CREATED);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<Integer> updateProduct(
            @PathVariable(name = "productId") final Integer productId,
            @RequestBody @Valid final ProductDTO productDTO) {
        productService.update(productId, productDTO);
        return ResponseEntity.ok(productId);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable(name = "productId") final Integer productId) {
        final ReferencedWarning referencedWarning = productService.getReferencedWarning(productId);
        if (referencedWarning != null) {
            throw new ReferencedException(referencedWarning);
        }
        productService.delete(productId);
        return ResponseEntity.noContent().build();
    }

}
