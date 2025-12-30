package com.nnikolaev.beercollection.controller;

import com.nnikolaev.beercollection.common.Constant;
import com.nnikolaev.beercollection.dto.request.UserPatchRequest;
import com.nnikolaev.beercollection.dto.response.UserDto;
import com.nnikolaev.beercollection.security.ResponseHandler;
import com.nnikolaev.beercollection.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.nnikolaev.beercollection.common.Constant.Route;

@RestController
@PreAuthorize(Constant.ADMIN_AUTHORIZATION)
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping(Route.User.READ_ONE)
    public ResponseEntity<UserDto> get(@PathVariable UUID id) {
        return ResponseHandler.success(this.userService.get(id));
    }

    @PatchMapping(Route.User.UPDATE)
    public ResponseEntity<UserDto> patch(@PathVariable UUID id, @RequestBody UserPatchRequest req) {
        return ResponseHandler.success(this.userService.update(id, req));
    }
}
