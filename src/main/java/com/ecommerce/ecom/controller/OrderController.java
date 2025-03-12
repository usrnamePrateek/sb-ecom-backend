package com.ecommerce.ecom.controller;


import com.ecommerce.ecom.dto.OrderDTO;
import com.ecommerce.ecom.dto.OrderRequestDTO;
import com.ecommerce.ecom.dto.StripeRequestDTO;
import com.ecommerce.ecom.service.OrderService;
import com.ecommerce.ecom.service.StripeService;
import com.ecommerce.ecom.util.AuthUtil;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class OrderController {

    private final OrderService orderService;
    private final AuthUtil authUtil;
    private final StripeService stripeService;

    OrderController(OrderService orderService, AuthUtil authUtil, StripeService stripeService) {
        this.orderService = orderService;
        this.authUtil = authUtil;
        this.stripeService = stripeService;
    }

    @PostMapping("/order/users/payments")
    public ResponseEntity<OrderDTO> orderProducts( @RequestBody OrderRequestDTO orderRequestDTO) {
        String emailId = authUtil.loggedInEmail();
        OrderDTO order = orderService.placeOrder(
                emailId,
                orderRequestDTO.getAddressId(),
                orderRequestDTO.getPaymentMethod(),
                orderRequestDTO.getPgName(),
                orderRequestDTO.getPgPaymentId(),
                orderRequestDTO.getPgStatus(),
                orderRequestDTO.getPgResponseMessage()
        );
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    @PostMapping("/order/create-client-secret")
    public ResponseEntity<String> createStripeSecret( @RequestBody StripeRequestDTO stripeRequestDTO) throws StripeException {
        PaymentIntent paymentIntent = stripeService.createPaymentIntent(stripeRequestDTO);
        return new ResponseEntity<>(paymentIntent.getClientSecret(), HttpStatus.CREATED);
    }
}
