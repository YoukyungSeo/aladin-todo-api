
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
| 메서드 | 엔드포인트      | 설명               |
|--------|-----------------|------------------|
| POST   | /users/signup   | 회원가입          |
| POST   | /users/login    | 로그인 (JWT 발급)  |
| GET    | /users/me       | 사용자 정보 조회       |
| PUT    | /users/me       | 사용자 정보 수정       |
| DELETE | /users/me       | 회원 탈퇴          |

### TODO 관련 API
| 메서드 | 엔드포인트           | 설명                   |
|--------|----------------------|----------------------|
| POST   | /todos                | 할일 등록              |
| GET    | /todos                | 할일 목록 조회          |
| GET    | /todos/{id}           | 특정 할일 조회          |
| PUT    | /todos/{id}           | 특정 할일 수정          |
| DELETE | /todos/{id}           | 특정 할일 삭제          |
| GET    | /todos/search         | 할일 검색 (제목/내용)   |

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
