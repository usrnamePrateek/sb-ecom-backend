package com.ecommerce.ecom.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Table(name = "address")
@Data
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private long addressId;

    @NotBlank
    @Size(min = 5, message = "street name must be at least five characters")
    private String street;

    @NotBlank
    @Size(min = 5, message = "building name must be at least five characters")
    private String buildingName;

    @NotBlank
    @Size(min = 4, message = "city name must be at least five characters")
    private String city;

    @NotBlank
    @Size(min = 2, message = "state name must be at least two characters")
    private String state;

    @NotBlank
    @Size(min = 2, message = "country name must be at least two characters")
    private String country;

    @NotBlank
    @Size(min = 6, message = "zipcode must be at least six characters")
    private String zipcode;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Address(String street, String buildingName, String city, String state, String country, String zipcode) {
        this.street = street;
        this.buildingName = buildingName;
        this.city = city;
        this.state = state;
        this.country = country;
        this.zipcode = zipcode;
    }
}
