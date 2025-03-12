package com.ecommerce.ecom.service;

import com.ecommerce.ecom.dto.CartDTO;
import com.ecommerce.ecom.dto.CartItemDTO;
import com.ecommerce.ecom.dto.ProductDTO;
import com.ecommerce.ecom.entity.Cart;
import com.ecommerce.ecom.entity.CartItem;
import com.ecommerce.ecom.entity.Product;
import com.ecommerce.ecom.exceptions.model.ApiException;
import com.ecommerce.ecom.repositories.CartItemRepository;
import com.ecommerce.ecom.repositories.CartRepository;
import com.ecommerce.ecom.repositories.ProductRepository;
import com.ecommerce.ecom.repositories.UserRepository;
import com.ecommerce.ecom.security.services.UserDetailsImpl;
import com.ecommerce.ecom.util.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final AuthUtil authUtil;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final ModelMapper modelMapper;

    CartService(CartRepository cartRepository, AuthUtil authUtil,
                ProductRepository productRepository,
                CartItemRepository cartItemRepository,
                ModelMapper modelMapper) {
        this.cartRepository = cartRepository;
        this.authUtil = authUtil;
        this.productRepository = productRepository;
        this.cartItemRepository = cartItemRepository;
        this.modelMapper = modelMapper;
    }

    public void addProductToCart(Long productId, Integer quantity) {
        Cart userCart = createCart();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ApiException("product does not exist", HttpStatus.BAD_REQUEST));

        if (product.getQuantity() == 0 || product.getQuantity() < quantity) {
            throw new ApiException("product not available in that quantity", HttpStatus.BAD_REQUEST);
        }

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(userCart.getCartId(), productId);

        if (cartItem != null) {
            throw new ApiException("item already exists in cart", HttpStatus.BAD_REQUEST);
        }

        cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(quantity);
        cartItem.setCart(userCart);
        cartItem.setDiscount(product.getDiscount());
        cartItem.setProductPrice(product.getSpecialPrice());
        cartItemRepository.save(cartItem);
    }

    private Cart createCart() {
        Cart cart = cartRepository.findCartByEmail((authUtil.loggedInEmail()));

        if (cart == null) {
            Cart newCart = new Cart();
            newCart.setUser(authUtil.loggedInUser());
            return cartRepository.save(newCart);
        }

        return cart;
    }

    public CartDTO getCart() {
        Cart cart = cartRepository.findCartByEmail(authUtil.loggedInEmail());

        if (cart == null) {
            return null;
        }

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        List<ProductDTO> products = cart.getCartItems().stream().map(item -> {
            ProductDTO productDTO = modelMapper.map(item.getProduct(), ProductDTO.class);
            productDTO.setQuantity(item.getQuantity());
            return productDTO;
        }).toList();

        cartDTO.setProducts(products);
        return cartDTO;
    }

    public void updateCart(Long productId, String operation) {
        Cart userCart = cartRepository.findCartByEmail(authUtil.loggedInEmail());
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(userCart.getCartId(), productId);

        if(cartItem == null){
            throw new ApiException("product does not exist in cart", HttpStatus.BAD_REQUEST);
        }

        int quan = "delete".equals(operation) ? -1 : 1;

        cartItem.setQuantity(cartItem.getQuantity() + quan);
        cartItemRepository.save(cartItem);
    }

    public void deleteItemFromCart(Long productId) {
        Cart userCart = cartRepository.findCartByEmail(authUtil.loggedInEmail());
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(userCart.getCartId(), productId);
        if (cartItem == null) {
            throw new ApiException("product does not exist in cart", HttpStatus.BAD_REQUEST);
        }
        cartItemRepository.delete(cartItem);
    }

    public void deleteCart() {
        Cart userCart = cartRepository.findCartByEmail(authUtil.loggedInEmail());
        if (userCart == null) {
            throw new ApiException("Cart does not exist", HttpStatus.BAD_REQUEST);
        }
        cartRepository.delete(userCart);
    }

    @Transactional
    public String createOrUpdateCartWithItems(List<CartItemDTO> cartItems) {
        // Get user's email
        String emailId = authUtil.loggedInEmail();

        // Check if an existing cart is available or create a new one
        Cart existingCart = cartRepository.findCartByEmail(emailId);
        if (existingCart == null) {
            existingCart = new Cart();
            existingCart.setUser(authUtil.loggedInUser());
            existingCart = cartRepository.save(existingCart);
        } else {
            // Clear all current items in the existing cart
            cartItemRepository.deleteAllByCartId(existingCart.getCartId());
        }

        double totalPrice = 0.00;

        // Process each item in the request to add to the cart
        for (CartItemDTO cartItemDTO : cartItems) {
            Long productId = cartItemDTO.getProductId();
            Integer quantity = cartItemDTO.getQuantity();

            // Find the product by ID
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ApiException("productId does not exist", HttpStatus.BAD_REQUEST));

            // Directly update product stock and total price
            // product.setQuantity(product.getQuantity() - quantity);
            totalPrice += product.getSpecialPrice() * quantity;

            // Create and save cart item
            CartItem cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setCart(existingCart);
            cartItem.setQuantity(quantity);
            cartItem.setProductPrice(product.getSpecialPrice());
            cartItem.setDiscount(product.getDiscount());
            cartItemRepository.save(cartItem);
        }

        // Update the cart's total price and save
        cartRepository.save(existingCart);
        return "Cart created/updated with the new items successfully";
    }
}
