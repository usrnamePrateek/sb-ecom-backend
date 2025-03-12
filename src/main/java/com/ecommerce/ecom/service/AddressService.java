package com.ecommerce.ecom.service;

import com.ecommerce.ecom.dto.AddressDTO;
import com.ecommerce.ecom.entity.Address;
import com.ecommerce.ecom.entity.User;
import com.ecommerce.ecom.exceptions.model.ApiException;
import com.ecommerce.ecom.repositories.AddressRepository;
import com.ecommerce.ecom.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressService {
    private final ModelMapper modelMapper;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public AddressService(ModelMapper modelMapper, AddressRepository addressRepository, UserRepository userRepository) {
        this.modelMapper = modelMapper;
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    public AddressDTO createAddress(AddressDTO addressDTO, User user) {
        Address address = modelMapper.map(addressDTO, Address.class);
        address.setUser(user);
        Address savedAddress = addressRepository.save(address);

        List<Address> addressList = user.getAddresses();
        addressList.add(address);
        user.setAddresses(addressList);
        userRepository.save(user);

        return modelMapper.map(savedAddress, AddressDTO.class);
    }

    public List<AddressDTO> getAllAddresses() {
        return addressRepository.findAll().stream()
                .map(address -> modelMapper.map(address, AddressDTO.class))
                .toList();
    }

    public AddressDTO getAddress(Long addressId) {
        Address address = addressRepository.findById(addressId).orElseThrow(() -> new ApiException("Address not found"
                , HttpStatus.BAD_REQUEST));

        return modelMapper.map(address, AddressDTO.class);
    }

    public List<AddressDTO> getAddress(User user) {
        List<Address> address = addressRepository.findAddressByUserId(user.getUserId())
                .orElseThrow(() -> new ApiException("Address not found", HttpStatus.BAD_REQUEST));

        return address.stream().map(add->modelMapper.map(add, AddressDTO.class)).toList();
    }

    public AddressDTO updateAddress(AddressDTO addressDTO, User user) {
        Address address =
                addressRepository.findAddressByAddressIdAndUserId(addressDTO.getAddressId(), user.getUserId())
                        .orElseThrow(() -> new ApiException(
                "Address not found", HttpStatus.BAD_REQUEST));

        address.setCity(addressDTO.getCity());
        address.setState(addressDTO.getState());
        address.setCountry(addressDTO.getCountry());
        address.setStreet(addressDTO.getStreet());
        address.setBuildingName(addressDTO.getBuildingName());
        address.setZipcode(addressDTO.getZipcode());

        Address savedAddress =addressRepository.save(address);
        return modelMapper.map(savedAddress, AddressDTO.class);
    }

    public void deleteAddress(Long addressId, User user) {
        Address address =
                addressRepository.findAddressByAddressIdAndUserId(addressId, user.getUserId())
                        .orElseThrow(() -> new ApiException(
                                "Address not found", HttpStatus.BAD_REQUEST));

        addressRepository.delete(address);
    }
}
