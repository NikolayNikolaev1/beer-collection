package com.nnikolaev.beercollection.service;

import com.nnikolaev.beercollection.dto.request.CompanyUpsertDto;
import com.nnikolaev.beercollection.dto.response.CompanyDto;
import com.nnikolaev.beercollection.exception.CompanyExistsException;
import com.nnikolaev.beercollection.model.Company;
import com.nnikolaev.beercollection.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompanyService {
    @Autowired
    private CompanyRepository companyRepository;

    public CompanyDto create(CompanyUpsertDto dto)
            throws CompanyExistsException {
        final boolean nameExists = this.companyRepository.findByName(dto.name()).isPresent();

        if (nameExists) throw new CompanyExistsException();

        final Company newCompany = new Company(dto.name(), dto.description());

        this.companyRepository.save(newCompany);

        return new CompanyDto(
                newCompany.getId(),
                newCompany.getName(),
                newCompany.getDescription(),
                newCompany.getCreatedAt());
    }
}
