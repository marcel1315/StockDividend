package org.example.stockdiviend.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.stockdiviend.exception.impl.NoCompanyException;
import org.example.stockdiviend.model.Company;
import org.example.stockdiviend.model.Dividend;
import org.example.stockdiviend.model.ScrapedResult;
import org.example.stockdiviend.model.constants.CacheKey;
import org.example.stockdiviend.persist.entity.CompanyEntity;
import org.example.stockdiviend.persist.entity.DividendEntity;
import org.example.stockdiviend.persist.repository.CompanyRepository;
import org.example.stockdiviend.persist.repository.DividendRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class FinanceService {

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    @Cacheable(key = "#companyName", value = CacheKey.KEY_FINANCE)
    public ScrapedResult getDividendByCompanyName(String companyName) {
        log.info("search company -> " + companyName);
        // 1. 회사명을 기준으로 회사 정보를 조회
        CompanyEntity company = this.companyRepository.findByName(companyName)
                .orElseThrow(NoCompanyException::new);

        // 2. 조회된 회사의 ID로 배당금 정보 조회
        List<DividendEntity> dividendEntities = this.dividendRepository.findAllByCompanyId(company.getId());

        // 3. 결과 조합 후 반환
        List<Dividend> dividends = new ArrayList<>();
        for (DividendEntity e : dividendEntities) {
            dividends.add(new Dividend(e.getDate(), e.getDividend()));
        }

        return new ScrapedResult(
                new Company(company.getTicker(), company.getName()),
                dividends
        );
    }
}
