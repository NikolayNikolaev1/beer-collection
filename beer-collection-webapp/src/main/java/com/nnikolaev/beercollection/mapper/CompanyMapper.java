package com.nnikolaev.beercollection.mapper;

import com.nnikolaev.beercollection.dto.request.QueryParamsDto;
import com.nnikolaev.beercollection.dto.response.CompanyDto;
import com.nnikolaev.beercollection.model.Company;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class CompanyMapper {
    public CompanyDto map(Company company) {
        if (company == null) return null;

        return new CompanyDto(
                company.getId(),
                company.getName(),
                company.getDescription(),
                company.getUpdatedAt()
        );
    }

    public Page<CompanyDto> map(Page<Company> companies) {
        return companies.map(this::map);
    }

    public Specification<Company> mapSpecifications(QueryParamsDto params) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            if (params.search() != null) {
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("name")),
                                "%" + params.search().toLowerCase() + "%"));
            }

            return predicate;
        };
    }
}
