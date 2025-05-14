
# 📌 JWT 인증 기반 Todo 관리 API

> 알라딘 커뮤니케이션 과제(25.05.12 ~ 25.05.14)

---

## 📝 프로젝트 개요
- JWT 인증 기반 TODO 관리 REST API 구현.
- 회원 기능과 TODO 기능을 분리하여 설계.
- SQLite3 & Spring Data JPA 기반 로컬 개발 환경.
- API 명세, 테스트 코드, 프롬프트 포함.

---

## 🏗️ 기술 스택
| 항목              | 버전 / 설명                         |
|-------------------|-------------------------------------|
| Java              | 8                                  |
| Spring Boot       | 2.7.18                               |
| Spring Security   | JWT 인증 구현                       |
| SQLite3           | 파일형식 DB             |
| Maven             | 빌드 및 의존성 관리                 |
| JUnit5 / Mockmvc  | 테스트 도구                 |
| Jacoco            | 테스트 커버리지 측정 도구           |

---

## ✅ 기능 목록
### 사용자 관련 API
| 기능명 | Method | URL | 요청 값 | 비고 |
|---------|--------|-----|---------|-----|
| **회원가입** | POST | `/users/signup` | userId, username, password, email, phoneNo | 인증 불필요 |
| **로그인 (JWT 발급)** | POST | `/users/login` | userId, password | 인증 불필요, accessToken 반환 |
| **내 정보 조회** | GET | `/users/me` | - | 인증 필요 (Authorization 헤더) |
| **내 정보 수정** | PUT | `/users/me` | username, password, email, phoneNo | 인증 필요 |
| **회원 탈퇴** | DELETE | `/users/me` | password | 인증 필요 |

### 사용자 관련 API
| 기능명 | Method | URL | 요청 값 | 비고 |
|---------|--------|-----|---------|-----|
| **할일 등록** | POST | `/todos` | title, description, status | 인증 필요 |
| **할일 목록 조회** | GET | `/todos` | - | 인증 필요 |
| **특정 할일 조회** | GET | `/todos/{id}` | - | 인증 필요, PathVariable: id |
| **특정 할일 수정** | PUT | `/todos/{id}` | title, description, status | 인증 필요, PathVariable: id |
| **특정 할일 삭제** | DELETE | `/todos/{id}` | - | 인증 필요, PathVariable: id |
| **할일 검색** | GET | `/todos/search` | searchType, searchWord | 인증 필요, QueryParam |

### 공통
- 모든 API는 기본적으로 **JSON 형식** 요청/응답 사용.
- 인증이 필요한 API는 반드시 **`Authorization: Bearer <accessToken>`** 헤더 포함.
- `/users/signup`, `users/login` 제외.
- 기본 응답 포맷 예시:
    ```json
    {
      "status": 200,
      "message": "성공 메시지",
      "data": { ... }
    }
    ```
- 예외 발생 시 응답 예시:
  - **400 Bad Request** : 입력값 오류 (필수값 누락, 형식 오류 등)
  - **401 Unauthorized** : JWT 누락, 만료, 잘못된 토큰
  - **404 Not Found** : 존재하지 않는 값 요청
---

## ⚙️ 실행 방법

### 1. SQLite3 초기화
- `application.yml`에서 DB 설정 및 JPA 옵션(`create-drop`, `update`)으로 자동 초기화.
- DB 테이블 DDL `src/main/resources/schema.sql` 포함.

### 2. 애플리케이션 실행
```bash
mvn spring-boot:run
```

---

## 🧪 테스트 및 커버리지 확인

### 1. 테스트 실행
```bash
mvn clean test
```

### 2. Jacoco 커버리지 리포트 생성
```bash
mvn clean verify
```

### 3. 커버리지 리포트 확인
- 경로: `target/site/jacoco/index.html`
- 브라우저로 열어 시각화된 리포트 확인 가능.

### 4. 커버리지 목표
- 전체 코드 라인 커버리지 80% 이상 충족.
- 필수 테스트:
  - 회원가입 → 로그인 → JWT 인증 흐름 ✅
  - TODO 생성 → 목록조회 → 수정 → 삭제 ✅
  - JWT 없이 접근 시 401 응답 확인 ✅
  - 유효하지 않은 ID 접근 시 404 응답 확인 ✅

---

## ✨ JWT 인증 흐름 다이어그램

### PlantUML 코드
```
@startuml
actor Client
participant "UserController" as Controller
participant "UserService" as Service
participant "TokenProvider" as TokenProvider
participant "JwtAuthenticationFilter" as Filter
participant "SecurityContext" as SecurityContext

== 로그인 (Token 발급) ==
Client -> Controller : 로그인 요청 (id/password)
Controller -> Service : 인증 요청
Service -> Service : 비밀번호 검증
alt 비밀번호 검증 성공
    Service -> TokenProvider : JWT 발급 요청
    TokenProvider -> Service : AccessToken 반환
    Service -> Controller : AccessToken 반환
    Controller -> Client : AccessToken 전달
else 비밀번호 검증 실패
    Service -> Controller : 인증 실패 예외 발생 (401 Unauthorized)
    Controller -> Client : 401 Unauthorized 응답
end

== 인증 (API 요청 시 Token 검증) ==
Client -> Filter : API 요청 (Authorization: Bearer <token>)
Filter -> TokenProvider : 토큰 검증 & 사용자 정보 조회
alt 토큰 유효성 검증 성공
    TokenProvider --> Filter : 사용자 정보 반환
    Filter -> SecurityContext : 인증 객체 저장(Authentication)
    Filter -> Controller : 요청 전달
else 토큰 검증 실패
    TokenProvider --> Filter : 검증 실패 예외 발생 (401 Unauthorized)
    Filter -> Client : 401 Unauthorized 응답 반환
end
@enduml
```
[이미지로 확인하기](//www.plantuml.com/plantuml/png/bLDHQzDG57w_l-Am1qaUH0MVXXsh0y4z3RRXyoMvuCKiAJUtc_fKjSYaUr1Gw4exEb2PC25sAyQ_bDpy1s_J8ycfjRW-bEJmVUTyvpjVDcFDbMuV18nxEbAm5KWHQjQoHUd95WyrhEt7Gcr5eLPH40Yr1Zo6wlLlwAvGXz8J9GwVAMWlUYR27HKTIX_RaGe5Rn_fPbi_5Q4jSIsZy94CDDBcZnI_Axosalh5JA5uhb45gJ7MQ41-Fin-fcQKWbDEXpmPPkax5neDDZS1Rc_KzeJr6iKCFfh9Tt2aVwV5u_WeKhxBQbZBnCqjpHBCrn5o6DOf9F_LpPDEFZak-Ybaaux5CnxePNKmtH_PrPI1_LMzg8Esu_QJFTm81vS4Wg8IcfudulYF5VtYj4z642_-3QSUe8yKRSRTl7V9H12BvVlrFXSd5wk6ewE81DC_DgVfR5lpPWJE_RltO3_aDZEHaY-5xwuIjWYshJ_alIaJeL-c12SvpPt7wAETEfpxY89fOZ2JjdL5iNGSK0Pu7HuAhYpcWPwrsN0PSfQSiNWxBbubE0bkWNbzQGRVpDbxQ-Ed_EeQp7bI34xAi23M3CV5uCAcX0PcsVthSXSxrwzQwLluagfRPCd8JAujjsFElZZqYtLf4t9Bj8Ya0XUXULWj7y4tYGHo_oSEw4MPYatxrpu8VWC0)

## 🗂️ 폴더 구조
```
aladin-todo-api/
├── src/
│   ├── main/java/com/aladin/todo_api/
│   └── test/java/com/aladin/todo_api/
├── prompts/
│   └── used_prompts.md
├── target/site/jacoco/index.html (커버리지 리포트)
├── pom.xml
└── README.md
```

---

## ✋ 담당자
- 서유경 (s_yk@naver.com)
