package org.example.stockdiviend.scraper;

import org.example.stockdiviend.model.Company;
import org.example.stockdiviend.model.ScrapedResult;

public interface Scraper {
    Company scrapCompanyByTicker(String ticker);

    ScrapedResult scrap(Company company);
}
