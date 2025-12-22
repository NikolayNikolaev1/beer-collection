package com.nnikolaev.beercollection.model.seed;

import com.nnikolaev.beercollection.exception.CountryExistsException;
import com.nnikolaev.beercollection.service.CountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Component
public class CountrySeeder implements CommandLineRunner {
    private static final String ALL_COUNTRIES_API_URL = "https://restcountries.com/v3.1/all?fields=name,cca2";
    private static final String COUNTRY_NAME_KEY = "name";
    private static final String COUNTRY_COMMON_NAME_KEY = "common";
    private static final String COUNTRY_CODE_KEY = "cca2";
    @Autowired
    private CountryService countryService;

    @Override
    public void run(String... args) throws CountryExistsException {
        if (this.countryService.existsAny()) return;

        List<Map<String, Object>> countries = this.fetchAllCountriesWithCodes();
        this.addCountriesToDb(countries);
    }

    private List<Map<String, Object>> fetchAllCountriesWithCodes() {
        RestTemplate rt = new RestTemplate();
        ResponseEntity<List<Map<String, Object>>> resp =
                rt.exchange(ALL_COUNTRIES_API_URL,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<>() {
                        });

        return resp.getBody();
    }

    private void addCountriesToDb(List<Map<String, Object>> countries) {
        for (Map<String, Object> currentCountry : countries) {
            Map<String, Object> name = (Map<String, Object>) currentCountry.get(COUNTRY_NAME_KEY);
            String common = name != null ? (String) name.get(COUNTRY_COMMON_NAME_KEY) : null;
            String cca2 = (String) currentCountry.get(COUNTRY_CODE_KEY);

            if (common == null || cca2 == null) continue;

            this.countryService.create(common, cca2.toLowerCase());
        }
    }
}