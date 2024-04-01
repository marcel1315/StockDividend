package org.example.stockdiviend.exception.impl;

import org.example.stockdiviend.exception.AbstractException;
import org.springframework.http.HttpStatus;

public class TickerIsEmpty extends AbstractException {
    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }

    @Override
    public String getMessage() {
        return "Ticker 값이 비었습니다.";
    }
}
