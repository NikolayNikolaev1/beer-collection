package com.nnikolaev.beercollection.mapper;

import com.nnikolaev.beercollection.dto.response.UserDto;
import com.nnikolaev.beercollection.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserDto map(User user) {
        if (user == null) return null;

        return new UserDto(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                user.getDeletedAt() == null,
                user.getUpdatedAt()
        );
    }
}
