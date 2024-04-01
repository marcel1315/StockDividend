# 프로젝트

## 개요

- 미국 주식 배당금 정보를 제공하는 API 서비스를 개발

- Yahoo finance에서 배당금 정보를 스크래핑해서 DB에 저장

## 사용 기술

- Spring Boot 2.5.6

- Java 11

- H2 Database

- Redis 7.2.4

- JPA

- Jsoup

# API

## 회원가입 및 로그인 API

- 회원가입

    - POST /auth/signup

    - 요청 본문 예시

        ```json
        {
            "username":"admin",
            "password":"admin123!@#",
            "roles":[
                "ROLE_READ",
                "ROLE_WRITE"
            ]
        }
        ```
    
    - username, password, roles 모두 필수값임

    - 중복 username은 허용하지 않음. 400 status 코드와 적절한 에러 메시지 반환

- 로그인

    - POST /auth/signin

    - 요청 본문 예시

        ```json
        {
            "username":"admin",
            "password":"admin123!@#"
        }
        ```

    - 회원가입이 되어있고, 아이디/패스워드 정보가 옳은 경우 JWT 발급

## 회사 정보 API

- 새로운 회사 정보 추가 (ROLE_WRITE 필요)

    - POST /company

    - 요청 헤더
        ```json
        "Authorization": "Bearer [signin token]"
        ```

    - 요청 본문 예시

        ```json
        { 
            "ticker": "MMM"
        }
        ```
    
    - 추가하고자 하는 회사의 ticker 를 입력으로 받아 해당 회사의 정보를 스크래핑하고 저장
    
    - 이미 보유하고 있는 회사의 정보일 경우 400 status 코드와 적절한 에러 메시지 반환
    
    - 존재하지 않는 회사 ticker 일 경우 400 status 코드와 적절한 에러 메시지 반환


- 저장된 모든 회사 목록 조회

    - GET /company

    - 요청 헤더
        ```json
        "Authorization": "Bearer [signin token]"
        ```

    - 서비스에서 관리하고 있는 모든 회사 목록을 반환

    - 반환 결과는 Page 인터페이스 형태

- 회사 정보 삭제 (ROLE_WRITE 필요)

    - DELETE /company/{ticker}

    - 요청 헤더
        ```json
        "Authorization": "Bearer [signin token]"
        ```

    - ticker 에 해당하는 회사 정보 삭제

    - 삭제시 회사의 배당금 정보와 캐시도 모두 삭제

- 회사 이름 자동완성

    - GET /company/autocomplete?keyword={companyNamePrefix}

    - companyNamePrefix 부분에 회사 이름의 앞글자 입력

    - 앞글자가 동일한 회사명을 최대 10개까지 반환

## 배당금 조회 API

- 배당금 조회

    - GET /finance/dividend/{companyName}
    
    - 회사 이름을 인풋으로 받아서 해당 회사의 메타 정보와 배당금 정보를 반환

    - 잘못된 회사명이 입력으로 들어온 경우 400 status 코드와 에러메시지 반환
