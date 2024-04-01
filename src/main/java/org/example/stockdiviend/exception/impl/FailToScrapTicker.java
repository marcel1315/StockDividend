package org.example.stockdiviend.exception.impl;

import org.example.stockdiviend.exception.AbstractException;
import org.springframework.http.HttpStatus;

public class FailToScrapTicker extends AbstractException {

    private final String ticker;

    public FailToScrapTicker(String ticker) {
        this.ticker = ticker;
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }

    @Override
    public String getMessage() {
        return "Ticker 를 불러오는데 실패했습니다. Ticker 이름을 다시 확인해주세요. -> " + ticker;
    }
}
