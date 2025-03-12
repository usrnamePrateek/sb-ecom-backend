package com.ecommerce.ecom.repositories;

import com.ecommerce.ecom.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    @Query("select c from Cart c where c.user.email = :email")
    Cart findCartByEmail(String email);
}
