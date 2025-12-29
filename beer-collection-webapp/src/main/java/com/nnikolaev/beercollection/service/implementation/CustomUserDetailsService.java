package com.nnikolaev.beercollection.service.implementation;

import com.nnikolaev.beercollection.exception.user.UserNotFoundException;
import com.nnikolaev.beercollection.model.User;
import com.nnikolaev.beercollection.repository.UserRepository;
import com.nnikolaev.beercollection.security.CustomUserDetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public CustomUserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        final User user = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(String.format("User not found with email: %s", email)));

        return new CustomUserDetails(user);
    }
}
