package com.nnikolaev.beercollection.mapper;

import com.nnikolaev.beercollection.dto.response.BoxDto;
import com.nnikolaev.beercollection.model.Beer;
import com.nnikolaev.beercollection.model.Box;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class BoxMapper {
    @Autowired
    private BeerMapper beerMapper;

    public BoxDto map(Box box) {
        if (box == null) return null;

        return new BoxDto(
                box.getId(),
                box.getName(),
                box.getDescription(),
                box.getPrice(),
                box.getDeletedAt() != null,
                box.getUpdatedAt(),
                box.getTags().stream().toList(),
                this.beerMapper.map(box.getBeers().stream().toList())
        );
    }

    public List<BoxDto> map(List<Box> boxes) {
        return boxes
                .stream()
                .map(this::map)
                .toList();
    }
}
