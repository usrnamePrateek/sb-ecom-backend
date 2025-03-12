package com.ecommerce.ecom.controller;

import com.ecommerce.ecom.dto.CartDTO;
import com.ecommerce.ecom.dto.CartItemDTO;
import com.ecommerce.ecom.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CartController {

    private final CartService cartService;

    CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/carts/products/{productId}/quantity/{quantity}")
    public ResponseEntity<String> addProductToCart(@PathVariable Long productId, @PathVariable Integer quantity) {
        cartService.addProductToCart(productId, quantity);
        return ResponseEntity.status(HttpStatus.OK).body("cart item added successfully");
    }

    @GetMapping("/cart")
    public ResponseEntity<CartDTO> getCart() {
        CartDTO cart = cartService.getCart();
        return cart == null ? ResponseEntity.status(HttpStatus.NO_CONTENT).body(null)
                : ResponseEntity.ok(cart);
    }

    @PutMapping("/cart/products/{productId}/quantity/{operation}")
    public ResponseEntity<String> updateCart(@PathVariable Long productId, @PathVariable String operation) {
        cartService.updateCart(productId, operation);
        return ResponseEntity.ok("cart updated successfully");
    }

    @DeleteMapping("/cart/products/{productId}")
    public ResponseEntity<String> deleteItemFromCart(@PathVariable Long productId){
        cartService.deleteItemFromCart(productId);
        return ResponseEntity.ok("item deleted successfully");
    }

    @PostMapping("/cart/create")
    public ResponseEntity<String> createOrUpdateCart(@RequestBody List<CartItemDTO> cartItems){
        String response = cartService.createOrUpdateCartWithItems(cartItems);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

}
