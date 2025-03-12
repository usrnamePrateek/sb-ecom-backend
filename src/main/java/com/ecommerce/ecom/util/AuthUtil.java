package com.ecommerce.ecom.util;

import com.ecommerce.ecom.entity.User;
import com.ecommerce.ecom.exceptions.model.ApiException;
import com.ecommerce.ecom.repositories.UserRepository;
import com.ecommerce.ecom.security.services.UserDetailsImpl;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class AuthUtil {
    private final UserRepository userRepository;

    AuthUtil(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String loggedInEmail(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUserName(authentication.getName())
                .orElseThrow(()->new ApiException("", HttpStatus.UNAUTHORIZED));

        return user.getEmail();
    }

    public User loggedInUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUserName(authentication.getName())
                .orElseThrow(()->new ApiException("", HttpStatus.UNAUTHORIZED));
    }
}
