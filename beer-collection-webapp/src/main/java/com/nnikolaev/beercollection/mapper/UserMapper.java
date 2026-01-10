package com.nnikolaev.beercollection.mapper;

import com.nnikolaev.beercollection.dto.request.QueryParamsDto;
import com.nnikolaev.beercollection.dto.response.UserDto;
import com.nnikolaev.beercollection.model.User;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
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
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    public Page<UserDto> map(Page<User> users) {
        return users.map(this::map);
    }

    public Specification<User> mapSpecifications(QueryParamsDto params) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            if (params.search() != null) {
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("email")),
                                "%" + params.search().toLowerCase() + "%"));
            }

            if (params.role() != null) {
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.equal(
                                criteriaBuilder.upper(root.get("role")),
                                params.role().toString()));
            }

            return predicate;
        };
    }
}
