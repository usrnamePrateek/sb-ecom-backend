package com.ecommerce.ecom.repositories;

import com.ecommerce.ecom.config.AppConstants;
import com.ecommerce.ecom.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(AppConstants.AppRole appRole);
}
