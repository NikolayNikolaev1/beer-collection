package com.nnikolaev.beercollection.controller;

import com.nnikolaev.beercollection.dto.request.CompanyUpsertDto;
import com.nnikolaev.beercollection.security.ResponseHandler;
import com.nnikolaev.beercollection.service.CompanyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.nnikolaev.beercollection.common.Constant.Route;

@RestController
@PreAuthorize("hasRole('ADMIN')")
public class CompanyController {
    @Autowired
    private CompanyService companyService;

    @PostMapping(Route.Company.CREATE)
    public ResponseEntity<?> create(@Valid @RequestBody CompanyUpsertDto req) {
        return ResponseHandler.success(companyService.create(req));
    }

    @PutMapping(Route.Company.UPDATE)
    public ResponseEntity<?> update(@RequestParam String id, @Valid @RequestBody CompanyUpsertDto req) {
        return ResponseHandler.success(companyService.update(id, req));
    }
}
