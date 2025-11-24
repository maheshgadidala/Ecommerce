package com.JavaEcommerce.Ecommerce.util;


import com.JavaEcommerce.Ecommerce.model.User;
import com.JavaEcommerce.Ecommerce.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class AuthUtils {

    @Autowired
    UserRepository userRepository;
    /**
     * Return currently logged in user's email.
     * This is a minimal placeholder implementation. In a real application
     * this should read from the SecurityContext or token.
     */
    public String loggedInUserEmail() {

        Authentication authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        User user=userRepository.findByUserName(authentication.getName()).orElseThrow(()->new UsernameNotFoundException("User not found"));
        return user.getUserEmail();
        //
       // placeholder: return empty string to avoid NPEs
    }

    public String loggedInUsername() {

        Authentication authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        User user=userRepository.findByUserName(authentication.getName()).orElseThrow(()->new UsernameNotFoundException("User not found"));
        return user.getUserName();
        //
       // placeholder: return empty string to avoid NPEs
    }
    public Long loggedInUserId() {

        Authentication authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        User user=userRepository.findByUserName(authentication.getName()).orElseThrow(()->new UsernameNotFoundException("User not found"));
        return user.getUserid();
        //
       // placeholder: return empty string to avoid NPEs
    }
}
