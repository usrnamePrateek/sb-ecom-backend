package com.ecommerce.ecom.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ecommerce.ecom.entity.Category;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByCategoryName(String categoryName);
}
