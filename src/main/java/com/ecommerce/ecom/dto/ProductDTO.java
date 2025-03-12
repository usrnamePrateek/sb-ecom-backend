package com.ecommerce.ecom.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long productId;

    @NotBlank
    @Size(min = 3, message = "product name should be at least 3 characters")
    private String productName;

    @NotBlank
    @Size(min = 6, message = "description should be at least 6 characters")
    private String description;
    private String image;

    @Min(value = 0, message = "quantity can't be less than zero")
    private Integer quantity;

    @Min(value = 0, message = "price can't be less than zero")
    private Double price;

    @Min(value = 0, message = "discount can't be less than zero")
    @Max(value = 100, message = "discount can't be more than 100")
    private Double discount;

    private Double specialPrice;

}
