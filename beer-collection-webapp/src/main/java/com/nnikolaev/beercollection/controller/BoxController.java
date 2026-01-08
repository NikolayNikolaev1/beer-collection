package com.nnikolaev.beercollection.controller;

import com.nnikolaev.beercollection.common.Constant;
import com.nnikolaev.beercollection.dto.request.BoxUpsertDto;
import com.nnikolaev.beercollection.dto.response.BoxDto;
import com.nnikolaev.beercollection.security.ResponseHandler;
import com.nnikolaev.beercollection.service.BoxService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.nnikolaev.beercollection.common.Constant.Route;

@RestController
public class BoxController {
    @Autowired
    private BoxService boxService;

    @PostMapping(Route.Box.CREATE)
    @PreAuthorize(Constant.ADMIN_AUTHORIZATION)
    public ResponseEntity<BoxDto> create(@Valid @RequestBody BoxUpsertDto req) {
        // TODO: Test this endpoint.
        return ResponseHandler.success(this.boxService.create(req));
    }
}
