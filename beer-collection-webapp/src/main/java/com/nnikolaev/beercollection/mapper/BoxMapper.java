package com.nnikolaev.beercollection.mapper;

import com.nnikolaev.beercollection.dto.response.BoxDto;
import com.nnikolaev.beercollection.model.Box;
import org.springframework.stereotype.Component;

@Component
public class BoxMapper {
    public BoxDto map(Box box) {
        if (box == null) return null;

        return new BoxDto(
                box.getId(),
                box.getName(),
                box.getDescription(),
                box.getPrice(),
                box.getDeletedAt() == null
        );
    }
}
