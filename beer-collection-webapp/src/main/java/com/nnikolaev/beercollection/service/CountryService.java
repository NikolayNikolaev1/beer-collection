package com.nnikolaev.beercollection.service;

import com.nnikolaev.beercollection.exception.CountryExistsException;
import com.nnikolaev.beercollection.model.Country;
import com.nnikolaev.beercollection.repository.CountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CountryService {
    @Autowired
    private CountryRepository countryRepository;

    public boolean existsAny() {
        return this.countryRepository.count() > 0;
    }

    public void create(String name, String code) {
        final boolean countryExists = this.countryRepository
                .findByNameOrCode(name, code)
                .isPresent();

        if (countryExists) {
            throw new CountryExistsException(String.format("Country with name %s or code %s already exists.", name, code));
        }

        final Country country = new Country(name, code);
        this.countryRepository.save(country);
    }
}
