package com.nnikolaev.beercollection.service.implementation;

import com.nnikolaev.beercollection.dto.request.QueryParamsDto;
import com.nnikolaev.beercollection.dto.response.UserDto;
import com.nnikolaev.beercollection.exception.user.*;
import com.nnikolaev.beercollection.mapper.UserMapper;
import com.nnikolaev.beercollection.model.User;
import com.nnikolaev.beercollection.model.enums.UserRole;
import com.nnikolaev.beercollection.repository.UserRepository;
import com.nnikolaev.beercollection.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public void create(String email, String password, UserRole role)
            throws UserExistsException {
        final boolean emailExists = this.userRepository.findByEmail(email).isPresent();

        if (emailExists) throw new UserExistsException();

        final String hashedPassword = this.passwordEncoder.encode(password);
        final User user = new User(email, hashedPassword, role);

        this.userRepository.save(user);
    }

    public Page<UserDto> getAll(QueryParamsDto params, Pageable pageable) {
        return null;
    }

    public UserDto get(UUID id) {
        return this.userMapper.map(this.getById(id));
    }

    public Void deactivate(UUID id) {
        User user = this.getById(id);

        // TODO: Add already deleted check exception.

        user.setDeletedAt(Instant.now());
//        this.userRepository.save(user); // TODO: Check if save is needed for update

        return null;
    }

    public User getById(UUID id)
            throws UsernameNotFoundException {
        return this.userRepository
                .findById(id)
                .orElseThrow(UserNotFoundException::new);
    }
}
