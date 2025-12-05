package com.nnikolaev.beercollection.service;

import com.nnikolaev.beercollection.dto.AuthRequestDto;
import com.nnikolaev.beercollection.exception.UserNotFoundException;
import com.nnikolaev.beercollection.model.User;
import com.nnikolaev.beercollection.model.enums.UserRole;
import com.nnikolaev.beercollection.repository.UserRepository;
import com.nnikolaev.beercollection.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenProvider tokenProvider;

    // maybe a TokenProvider for JWTs, and a token blacklist store if you plan logout

    public void register(AuthRequestDto requestDto) {
        if (userRepository.findByEmail(requestDto.email).isPresent()) {
//            throw new EmailAlreadyUsedException("Email is already taken");
        }

        String hashedPassword = passwordEncoder.encode(requestDto.password);

        User user = new User(requestDto.email, hashedPassword, UserRole.END_USER);
        userRepository.save(user);
    }

    public String login(AuthRequestDto requestDto) throws UserNotFoundException {
        final User user = userRepository
                .findByEmail(requestDto.email)
                .orElseThrow(UserNotFoundException::new);

        final boolean passwordMatch = passwordEncoder.matches(requestDto.password, user.getPassword());

        if (!passwordMatch) throw new UserNotFoundException();

        return this.tokenProvider.generateToken(user.getEmail());
    }

    // TODO: Add logout
}
