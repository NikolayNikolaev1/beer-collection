package com.nnikolaev.beercollection.service;

import com.nnikolaev.beercollection.dto.request.CompanyUpsertDto;
import com.nnikolaev.beercollection.dto.response.CompanyDto;
import com.nnikolaev.beercollection.exception.company.*;
import com.nnikolaev.beercollection.model.Company;
import com.nnikolaev.beercollection.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CompanyService {
    @Autowired
    private CompanyRepository companyRepository;

    public CompanyDto create(CompanyUpsertDto dto)
            throws CompanyExistsException {
        this.validateName(dto.name());

        final Company newCompany = new Company(dto.name(), dto.description());

        this.companyRepository.save(newCompany);

        return new CompanyDto(
                newCompany.getId(),
                newCompany.getName(),
                newCompany.getDescription(),
                newCompany.getCreatedAt());
    }

    // TODO: Check how would getById, getAll and deleteMultipleIds work in spring.
//    public Set<CompanyDto> getAll() {
//        List<Company> companies = this.companyRepository.findAll();
//
//
//    }

    public CompanyDto update(String id, CompanyUpsertDto dto)
            throws CompanyExistsException, CompanyNotFoundException {
        Company company = this.companyRepository
                .findById(UUID.fromString(id))
                .orElseThrow(CompanyNotFoundException::new);

        if (!company.getName().equals(dto.name())) this.validateName(dto.name());

        company.setName(dto.name());
        company.setDescription(dto.description());

        this.companyRepository.save(company);

        return new CompanyDto(
                company.getId(),
                company.getName(),
                company.getDescription(),
                company.getUpdatedAt());
    }

    private void validateName(String name)
            throws CompanyExistsException {
        final boolean nameExists = this.companyRepository.findByName(name).isPresent();

        if (nameExists) throw new CompanyExistsException();
    }
}
