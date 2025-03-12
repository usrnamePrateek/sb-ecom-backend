package com.ecommerce.ecom.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CartDTO {
    private Long cartId;
    private List<ProductDTO> products;
    private Double totalPrice;
}
