package com.ecommerce.ecom.service;

import com.ecommerce.ecom.dto.OrderDTO;
import com.ecommerce.ecom.dto.OrderItemDTO;
import com.ecommerce.ecom.entity.*;
import com.ecommerce.ecom.exceptions.model.ApiException;
import com.ecommerce.ecom.repositories.*;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService{

    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final CartService cartService;
    private final ModelMapper modelMapper;
    private final ProductRepository productRepository;

    OrderService(CartRepository cartRepository, AddressRepository addressRepository,
                 OrderItemRepository orderItemRepository, OrderRepository orderRepository,
                 PaymentRepository paymentRepository, CartService cartService, ModelMapper modelMapper, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.addressRepository = addressRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.cartService = cartService;
        this.modelMapper = modelMapper;
        this.productRepository = productRepository;


    }
@Transactional
public OrderDTO placeOrder(String emailId, Long addressId, String paymentMethod, String pgName,
                           String pgPaymentId, String pgStatus, String pgResponseMessage) {
        // get user cart
        Cart cart = cartRepository.findCartByEmail(emailId);

        if (cart == null) {
            throw new ApiException("Cart does not exist", HttpStatus.BAD_REQUEST);
        }

        if (cart.getCartItems().isEmpty()) {
            throw new ApiException("Cart is empty", HttpStatus.BAD_REQUEST);
        }

        // get user address
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ApiException("Address does not exist", HttpStatus.BAD_REQUEST));

        // create order
        Order order = new Order();
        order.setEmail(emailId);
        order.setOrderDate(LocalDate.now());
        order.setTotalAmount(cart.getCartItems().stream()
                .mapToDouble(item -> item.getProductPrice() * item.getQuantity()).sum());
        order.setOrderStatus("Order Accepted !");
        order.setAddress(address);

        // add payment details
        Payment payment = new Payment(paymentMethod, pgPaymentId, pgStatus, pgResponseMessage, pgName);
        payment.setOrder(order);
        payment = paymentRepository.save(payment);

        order.setPayment(payment);
        Order savedOrder = orderRepository.save(order);

        // save order items
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cart.getCartItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setDiscount(cartItem.getDiscount());
            orderItem.setOrderedProductPrice(cartItem.getProductPrice());
            orderItem.setOrder(savedOrder);
            orderItems.add(orderItem);
        }

        orderItems = orderItemRepository.saveAll(orderItems);

        // update product quantity
        for(CartItem item : cart.getCartItems()){
            int quantity = item.getQuantity();
            Product product = item.getProduct();

            // Reduce stock quantity
            product.setQuantity(product.getQuantity() - quantity);

            // Save product back to the database
            productRepository.save(product);

            // Remove items from cart
            cartService.deleteItemFromCart(item.getProduct().getProductId());
        }

        OrderDTO orderDTO = modelMapper.map(savedOrder, OrderDTO.class);
        orderItems.forEach(item -> orderDTO.getOrderItems()
                .add(modelMapper.map(item, OrderItemDTO.class)));
        orderDTO.setAddressId(addressId);

        return orderDTO;
    }
}
