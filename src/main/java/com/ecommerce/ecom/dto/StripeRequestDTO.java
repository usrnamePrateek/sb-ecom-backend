package com.ecommerce.ecom.dto;

import lombok.Data;

@Data
public class StripeRequestDTO {
    private Long amount;
    private String currency;
}
