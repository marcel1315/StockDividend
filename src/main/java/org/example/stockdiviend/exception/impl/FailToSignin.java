package org.example.stockdiviend.exception.impl;

import org.example.stockdiviend.exception.AbstractException;
import org.springframework.http.HttpStatus;

public class FailToSignin extends AbstractException {
    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }

    @Override
    public String getMessage() {
        return "로그인에 실패했습니다.";
    }
}
