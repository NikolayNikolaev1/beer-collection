package com.nnikolaev.beercollection.model.seed;

import com.nnikolaev.beercollection.model.Company;
import com.nnikolaev.beercollection.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(3)
public class CompanySeeder implements CommandLineRunner {
    @Autowired
    private CompanyRepository companyRepository;

    @Override
    public void run(String... args) {
        if (companyRepository.count() > 0) return;

        List<Company> companies = List.of(
                new Company("BrewDog", "Innovative craft brewery from Scotland known for bold flavors and global expansion."),
                new Company("Mikkeller", "Danish craft brewery famous for experimental beers and collaborations across Europe."),
                new Company("Brouwerij Westvleteren", "Belgian Trappist brewery producing some of the world’s most sought-after beers."),
                new Company("Weihenstephaner", "Germany’s oldest brewery, combining centuries of tradition with premium quality."),
                new Company("Birra Baladin", "Italian craft brewery blending traditional brewing with local Italian ingredients."),
                new Company("Põhjala Brewery", "Estonian craft brewery known for strong stouts and barrel-aged beers."),
                new Company("Brew By Numbers", "UK-based craft brewery focusing on numbered recipes and consistent quality."),
                new Company("Garage Beer Co", "Spanish craft brewery from Barcelona inspired by American craft beer culture."),
                new Company("To Øl", "Danish brewery pushing boundaries with modern interpretations of classic styles."),
                new Company("Lervig Aktiebryggeri", "Norwegian craft brewery recognized for rich stouts and experimental ales.")
        );

        companyRepository.saveAll(companies);
    }
}
