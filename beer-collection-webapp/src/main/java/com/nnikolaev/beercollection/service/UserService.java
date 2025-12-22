package com.nnikolaev.beercollection.service;

import com.nnikolaev.beercollection.exception.EmailAlreadyUsedException;
import com.nnikolaev.beercollection.model.User;
import com.nnikolaev.beercollection.model.enums.UserRole;
import com.nnikolaev.beercollection.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void create(String email, String password, UserRole role)
            throws EmailAlreadyUsedException {
        final boolean emailExists = this.userRepository.findByEmail(email).isPresent();

        if (emailExists) throw new EmailAlreadyUsedException();

        final String hashedPassword = this.passwordEncoder.encode(password);
        final User user = new User(email, hashedPassword, role);

        this.userRepository.save(user);
    }
}
