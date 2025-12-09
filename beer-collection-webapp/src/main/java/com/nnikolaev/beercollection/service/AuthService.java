package com.nnikolaev.beercollection.service;

import com.nnikolaev.beercollection.dto.request.AuthRequest;
import com.nnikolaev.beercollection.dto.response.AuthResponse;
import com.nnikolaev.beercollection.exception.*;
import com.nnikolaev.beercollection.model.User;
import com.nnikolaev.beercollection.model.enums.UserRole;
import com.nnikolaev.beercollection.repository.UserRepository;
import com.nnikolaev.beercollection.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenProvider tokenProvider;

    public Void register(AuthRequest requestDto) {
        this.userService.create(requestDto.email(), requestDto.password(), UserRole.END_USER);
//        final boolean emailExists = this.userRepository.findByEmail(requestDto.email()).isPresent();
//
//        if (emailExists) throw new EmailAlreadyUsedException();
//
//        final String hashedPassword = this.passwordEncoder.encode(requestDto.password());
//        final User user = new User(requestDto.email(), hashedPassword, UserRole.END_USER);
//
//        this.userRepository.save(user);

        return null;
    }

    public AuthResponse login(AuthRequest requestDto) {
        final User user = this.userRepository
                .findByEmail(requestDto.email())
                .orElseThrow(UserNotFoundException::new);

        final boolean passwordMatch = this.passwordEncoder.matches(requestDto.password(), user.getPassword());

        if (!passwordMatch) throw new UserNotFoundException();

        final String token = this.tokenProvider
                .generateToken(user.getEmail(), user.getRole().toString());

        return new AuthResponse(user.getEmail(), token);
    }

    public Void logout(String authHeader) {
        this.tokenProvider.blacklistToken(authHeader.substring(7));

        return null;
    }
}
