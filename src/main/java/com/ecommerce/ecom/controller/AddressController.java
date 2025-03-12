package com.ecommerce.ecom.controller;

import com.ecommerce.ecom.dto.AddressDTO;
import com.ecommerce.ecom.entity.User;
import com.ecommerce.ecom.service.AddressService;
import com.ecommerce.ecom.util.AuthUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api")
@RestController
public class AddressController {

    private final AuthUtil authUtil;
    private final AddressService addressService;

    public AddressController(AuthUtil authUtil, AddressService addressService) {
        this.authUtil = authUtil;
        this.addressService = addressService;
    }

    @PostMapping("/addresses")
    public ResponseEntity<AddressDTO> createAddress(@RequestBody AddressDTO addressDTO) {
        User user = authUtil.loggedInUser();
        AddressDTO savedAddressDTO = addressService.createAddress(addressDTO, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAddressDTO);
    }

    @GetMapping("/addresses")
    public ResponseEntity<List<AddressDTO>> getAllAddresses() {
        return ResponseEntity.ok(addressService.getAllAddresses());
    }

    @GetMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> getAddressById(@PathVariable Long addressId) {
        return ResponseEntity.ok(addressService.getAddress(addressId));
    }

    @GetMapping("/addresses/user")
    public ResponseEntity<List<AddressDTO>> getAddressForUser() {
        return ResponseEntity.ok(addressService.getAddress(authUtil.loggedInUser()));
    }

    @PutMapping("/addresses")
    public ResponseEntity<AddressDTO> updateAddress(@RequestBody AddressDTO addressDTO) {
        User user = authUtil.loggedInUser();
        AddressDTO savedAddressDTO = addressService.updateAddress(addressDTO, user);
        return ResponseEntity.status(HttpStatus.OK).body(savedAddressDTO);
    }

    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<String> deleteAddress(@PathVariable Long addressId) {
        User user = authUtil.loggedInUser();
        addressService.deleteAddress(addressId, user);
        return ResponseEntity.status(HttpStatus.OK).body("address deleted successfully");
    }
}
