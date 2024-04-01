package org.example.stockdiviend.service;

import lombok.AllArgsConstructor;
import org.apache.commons.collections4.Trie;
import org.example.stockdiviend.exception.impl.AlreadyExistTickerException;
import org.example.stockdiviend.exception.impl.FailToScrapTicker;
import org.example.stockdiviend.exception.impl.NoCompanyException;
import org.example.stockdiviend.model.Company;
import org.example.stockdiviend.model.ScrapedResult;
import org.example.stockdiviend.persist.entity.CompanyEntity;
import org.example.stockdiviend.persist.entity.DividendEntity;
import org.example.stockdiviend.persist.repository.CompanyRepository;
import org.example.stockdiviend.persist.repository.DividendRepository;
import org.example.stockdiviend.scraper.Scraper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CompanyService {

    private final Trie<String, String> trie;
    private final Scraper yahooFinanceScraper;

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    public Company save(String ticker) {
        boolean exists = this.companyRepository.existsByTicker(ticker);
        if (exists) {
            throw new AlreadyExistTickerException();
        }
        return this.storeCompanyAndDividend(ticker);
    }

    public Page<CompanyEntity> getAllCompany(Pageable pageable) {
        return this.companyRepository.findAll(pageable);
    }

    private Company storeCompanyAndDividend(String ticker) {
        // ticker 를 기준으로 회사를 스크래핑
        Company company = this.yahooFinanceScraper.scrapCompanyByTicker(ticker);
        if (ObjectUtils.isEmpty(company)) {
            throw new FailToScrapTicker(ticker);
        }

        // 해당 회사가 존재할 경우, 회사의 배당금 정보를 스크래핑
        ScrapedResult scrapedResult = this.yahooFinanceScraper.scrap(company);

        // 스크래핑 결과
        CompanyEntity companyEntity = this.companyRepository.save(new CompanyEntity(company));
        List<DividendEntity> dividendEntities = scrapedResult.getDividends().stream()
                .map(e -> new DividendEntity(companyEntity.getId(), e))
                .collect(Collectors.toList());
        this.dividendRepository.saveAll(dividendEntities);
        return company;
    }

    public List<String> getCompanyNameByKeyword(String keyword) {
        Pageable limit = PageRequest.of(0, 10);

        Page<CompanyEntity> companyEntities = this.companyRepository.findByNameStartingWithIgnoreCase(keyword, limit);
        return companyEntities.stream()
                .map(CompanyEntity::getName)
                .collect(Collectors.toList());
    }

    public void addAutocompleteKeyword(String keyword) {
        this.trie.put(keyword, null);
    }

    public void deleteAutocompleteKeyword(String keyword) {
        this.trie.remove(keyword);
    }

    public String deleteCompany(String ticker) {
        CompanyEntity companyEntity = companyRepository.findByTicker(ticker)
                .orElseThrow(NoCompanyException::new);

        dividendRepository.deleteAllByCompanyId(companyEntity.getId());
        companyRepository.delete(companyEntity);

        // Trie 에 저장된 회사 이름을 지워야 함
        deleteAutocompleteKeyword(companyEntity.getName());
        return companyEntity.getName();
    }
}
