package com.ecommerce.ecom.repositories;

import com.ecommerce.ecom.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    @Query("select a from Address a where a.user.userId = :userId")
    Optional<List<Address>> findAddressByUserId(Long userId);

    Optional<Object> findByAddressId(long addressId);

    @Query("select a from Address a where a.user.userId = :userId and a.addressId = :addressId")
    Optional<Address> findAddressByAddressIdAndUserId(long addressId, long userId);
}
