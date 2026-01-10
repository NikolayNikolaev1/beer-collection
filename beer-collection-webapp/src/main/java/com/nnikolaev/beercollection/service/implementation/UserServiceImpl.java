package com.nnikolaev.beercollection.service.implementation;

import com.nnikolaev.beercollection.dto.request.*;
import com.nnikolaev.beercollection.dto.response.UserDto;
import com.nnikolaev.beercollection.exception.OperationConflictException;
import com.nnikolaev.beercollection.exception.user.*;
import com.nnikolaev.beercollection.mapper.UserMapper;
import com.nnikolaev.beercollection.model.Company;
import com.nnikolaev.beercollection.model.User;
import com.nnikolaev.beercollection.model.enums.UserRole;
import com.nnikolaev.beercollection.repository.UserRepository;
import com.nnikolaev.beercollection.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

import static com.nnikolaev.beercollection.common.Constant.ExceptionMessage;

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
        Specification<User> spec = this.userMapper.mapSpecifications(params);

        Page<User> page = this.userRepository.findAll(spec, pageable);

        return this.userMapper.map(page);
    }

    public UserDto get(UUID id) {
        return this.userMapper.map(this.getById(id));
    }

    public UserDto update(UUID id, UserPatchRequest req) {
        User user = this.getById(id);

        if (req.role() != null) {
            if (user.getRole().equals(req.role())) {
                throw new OperationConflictException(ExceptionMessage.User.CONFLICT_ROLE);
            }

            user.setRole(req.role());
        }

        if (req.isDeleted() != null) {
            if (user.getDeletedAt() != null && req.isDeleted()) {
                throw new OperationConflictException(ExceptionMessage.User.CONFLICT_DELETED);
            }

            if (user.getDeletedAt() == null && !req.isDeleted()) {
                throw new OperationConflictException(ExceptionMessage.User.CONFLICT_NOT_DELETED);
            }

            user.setDeletedAt(req.isDeleted() ? Instant.now() : null);
        }

        this.userRepository.save(user);

        return this.userMapper.map(user);
    }

    public User getById(UUID id)
            throws UsernameNotFoundException {
        return this.userRepository
                .findById(id)
                .orElseThrow(UserNotFoundException::new);
    }
}
