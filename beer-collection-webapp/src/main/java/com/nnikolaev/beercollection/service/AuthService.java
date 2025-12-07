package com.nnikolaev.beercollection.service;

import com.nnikolaev.beercollection.dto.request.AuthRequest;
import com.nnikolaev.beercollection.dto.response.AuthResponse;
import com.nnikolaev.beercollection.exception.*;
import com.nnikolaev.beercollection.model.User;
import com.nnikolaev.beercollection.model.enums.UserRole;
import com.nnikolaev.beercollection.repository.UserRepository;
import com.nnikolaev.beercollection.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;


@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenProvider tokenProvider;

    // maybe a TokenProvider for JWTs, and a token blacklist store if you plan logout

    public Void register(AuthRequest requestDto) throws EmailAlreadyUsedException {
        final boolean emailExists = userRepository.findByEmail(requestDto.email()).isPresent();

        if (emailExists) throw new EmailAlreadyUsedException();

        final String hashedPassword = passwordEncoder.encode(requestDto.password());
        final User user = new User(requestDto.email(), hashedPassword, UserRole.END_USER);

        userRepository.save(user);

        return null;
    }

    public AuthResponse login(AuthRequest requestDto) throws UserNotFoundException {
        final User user = userRepository
                .findByEmail(requestDto.email())
                .orElseThrow(UserNotFoundException::new);

        final boolean passwordMatch = passwordEncoder.matches(requestDto.password(), user.getPassword());

        if (!passwordMatch) throw new UserNotFoundException();

        final String token = this.tokenProvider.generateToken(user.getEmail());

        return new AuthResponse(user.getEmail(), token);
    }

    public Void logout(String authHeader) {
        this.tokenProvider.blacklistToken(authHeader.substring(7));

        return null;
    }
}
