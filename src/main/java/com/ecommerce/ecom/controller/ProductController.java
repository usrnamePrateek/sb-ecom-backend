package com.ecommerce.ecom.controller;

import com.ecommerce.ecom.dto.ProductDTO;
import com.ecommerce.ecom.dto.ProductResponse;
import com.ecommerce.ecom.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.ecommerce.ecom.config.AppConstants.*;

@RestController
@RequestMapping("/api")
public class ProductController {

    private final ProductService productService;

    ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO> addProduct(@Valid @RequestBody ProductDTO productDTO,
                                                 @PathVariable Long categoryId) {
        ProductDTO addedProductDTO = productService.addProduct(categoryId, productDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(addedProductDTO);
    }

    @GetMapping("/public/products")
    public ResponseEntity<ProductResponse> getAllProducts(@RequestParam(name = "category",
                                                                     required = false) String category,
                                                          @RequestParam(name = "keyword",
                                                                  required = false) String keyword,
                                                          @RequestParam(name = "pageNumber",
                                                                defaultValue = PAGE_NUMBER) Integer pageNumber,
                                                          @RequestParam(name = "pageSize", defaultValue = PAGE_SIZE) Integer pageSize,
                                                          @RequestParam(name = "sortBy", defaultValue = SORT_PRODUCTS_BY) String sortBy,
                                                          @RequestParam(name = "sortOrder", defaultValue = SORT_ORDER) String sortOrder) {
        ProductResponse productResponse = productService.getAllProducts(pageNumber, pageSize, sortBy, sortOrder,
                category, keyword);
        return CollectionUtils.isEmpty(productResponse.getContent()) ?
                new ResponseEntity<>(HttpStatus.NO_CONTENT) :
                ResponseEntity.status(HttpStatus.OK).body(productResponse);
    }

    @GetMapping("/public/categories/{categoryId}/products")
    public ResponseEntity<ProductResponse> getAllProductsByCategory(@PathVariable Long categoryId,
                                                                    @RequestParam(name = "pageNumber", defaultValue = PAGE_NUMBER) Integer pageNumber,
                                                                    @RequestParam(name = "pageSize", defaultValue = PAGE_SIZE) Integer pageSize,
                                                                    @RequestParam(name = "sortBy", defaultValue = SORT_PRODUCTS_BY) String sortBy,
                                                                    @RequestParam(name = "sortOrder", defaultValue = SORT_ORDER) String sortOrder) {
        ProductResponse productResponse = productService.getAllProductsByCategory(categoryId, pageNumber, pageSize, sortBy, sortOrder);
        return CollectionUtils.isEmpty(productResponse.getContent()) ?
                new ResponseEntity<>(HttpStatus.NO_CONTENT) :
                ResponseEntity.status(HttpStatus.OK).body(productResponse);
    }

    @GetMapping("/public/products/keyword/{keyword}")
    public ResponseEntity<ProductResponse> getAllProductsByKeyword(@PathVariable String keyword,
                                                                   @RequestParam(name = "pageNumber", defaultValue = PAGE_NUMBER) Integer pageNumber,
                                                                   @RequestParam(name = "pageSize", defaultValue = PAGE_SIZE) Integer pageSize,
                                                                   @RequestParam(name = "sortBy", defaultValue = SORT_PRODUCTS_BY) String sortBy,
                                                                   @RequestParam(name = "sortOrder", defaultValue = SORT_ORDER) String sortOrder) {
        ProductResponse productResponse = productService.getAllProductsByKeyword(keyword.concat("%"), pageNumber, pageSize, sortBy, sortOrder);
        return CollectionUtils.isEmpty(productResponse.getContent()) ?
                new ResponseEntity<>(HttpStatus.NO_CONTENT) :
                ResponseEntity.status(HttpStatus.OK).body(productResponse);
    }

    @PutMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long productId,
                                                    @Valid @RequestBody ProductDTO productDTO) {
        ProductDTO updatedProductDTO = productService.updateProduct(productId, productDTO);
        return ResponseEntity.status(HttpStatus.OK).body(updatedProductDTO);
    }

    @DeleteMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> deleteProduct(@PathVariable Long productId) {
        ProductDTO productDTO = productService.deleteProduct(productId);
        return ResponseEntity.status(HttpStatus.OK).body(productDTO);
    }

    @PutMapping("/admin/products/{productId}/image")
    public ResponseEntity<ProductDTO> updateProductImage(@PathVariable Long productId,
                                                         @RequestParam MultipartFile imageFile) throws IOException {
        ProductDTO productDTO = productService.updateImage(productId, imageFile);
        return ResponseEntity.status(HttpStatus.OK).body(productDTO);
    }
}
