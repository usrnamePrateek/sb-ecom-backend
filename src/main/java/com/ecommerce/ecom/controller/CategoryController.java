package com.ecommerce.ecom.controller;

import com.ecommerce.ecom.dto.CategoryDTO;
import com.ecommerce.ecom.dto.CategoryResponse;
import com.ecommerce.ecom.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.ecommerce.ecom.config.AppConstants.*;

@RestController
@RequestMapping("/api")
public class CategoryController {

    private final CategoryService categoryService;

    CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/public/categories")
    public ResponseEntity<CategoryResponse>
    getCategories(@RequestParam(name = "pageNumber", defaultValue = PAGE_NUMBER) Integer pageNumber,
                  @RequestParam(name = "pageSize", defaultValue = PAGE_SIZE) Integer pageSize,
                  @RequestParam(name = "sortBy", defaultValue = SORT_CATEGORIES_BY) String sortBy,
                  @RequestParam(name = "sortOrder", defaultValue = SORT_ORDER) String sortOrder) {
        return ResponseEntity.ok(categoryService.getAllCategories(pageNumber, pageSize, sortBy, sortOrder));
    }

    @PostMapping("/public/categories")
    public ResponseEntity<String> addCategory(@Valid @RequestBody CategoryDTO category) {
        categoryService.addCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED).body("success");
    }

    @PutMapping("/public/categories/{categoryId}")
    public ResponseEntity<String> editCategory(@Valid @RequestBody CategoryDTO category, @PathVariable Long categoryId) {
        categoryService.editCategory(category, categoryId);
        return ResponseEntity.ok("edited successfully");
    }

    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<String> deleteCategory(@PathVariable("categoryId") Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.ok("successfully deleted");

    }
}
