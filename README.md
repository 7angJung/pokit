# 📚 Pokit - 공부 타이머 & 기록 서비스

> 집중력이 부족한 개발자가 직접 만든 공부 타이머 서비스

## 📌 프로젝트 소개

Pokit은 공부 시간을 타이머로 측정하고 기록하는 백엔드 서비스입니다.
쉬는 시간을 제외한 순수 공부 시간만 기록하며, 오늘/주간/월간 통계를 제공합니다.

## 🚀 배포 서버

https://pokit.up.railway.app

## 🛠 기술 스택

| 분류 | 기술 |
|------|------|
| Language | Java 17 |
| Framework | Spring Boot 3.5.14 |
| Build | Gradle |
| Auth | Firebase Authentication |
| DB (로컬) | H2 (In-Memory) |
| DB (배포) | Supabase (PostgreSQL) |
| 배포 | Railway |

## 📁 프로젝트 구조

```
com.jupeter.pokit
├── auth
│   ├── controller   # 회원가입/로그인 API
│   ├── service      # Firebase 인증 로직
│   ├── repository   # 유저 DB 조회
│   ├── entity       # User 테이블
│   └── dto          # 요청/응답 데이터
├── timer
│   ├── controller   # 타이머/공부기록 API
│   ├── service      # 공부기록 저장/조회 로직
│   ├── repository   # 공부기록 DB 조회
│   ├── entity       # StudyRecord 테이블
│   └── dto          # 요청/응답 데이터
├── common
│   └── filter       # FirebaseTokenFilter
└── config
    ├── FirebaseConfig    # Firebase 초기화
    └── SecurityConfig    # Spring Security 설정
```

## 🔑 API 명세

### Auth

| Method | URL | 설명 | 인증 |
|--------|-----|------|------|
| POST | /api/auth/signup | 회원가입 | 불필요 |
| POST | /api/auth/login | 로그인 | 불필요 |

**회원가입 요청**
```json
{
    "email": "test@test.com",
    "password": "123456",
    "nickname": "포킷유저"
}
```

**로그인 요청**
```json
{
    "email": "test@test.com",
    "password": "123456"
}
```

**로그인 응답** (Firebase 커스텀 토큰)
```
eyJhbGciOiJSUzI1NiJ9...
```

---

### Timer

> 모든 타이머 API는 Authorization 헤더에 Bearer 토큰 필요

| Method | URL | 설명 |
|--------|-----|------|
| POST | /api/timer/record | 공부기록 저장 |
| GET | /api/timer/records/{firebaseUid} | 전체 공부기록 조회 |
| GET | /api/timer/today/{firebaseUid} | 오늘 총 공부시간 조회 |
| GET | /api/timer/weekly/{firebaseUid} | 주간 총 공부시간 조회 |
| GET | /api/timer/monthly/{firebaseUid} | 월간 총 공부시간 조회 |

**공부기록 저장 요청**
```json
{
    "firebaseUid": "유저UID",
    "studyMinutes": 50,
    "startTime": "2026-04-27T10:00:00",
    "endTime": "2026-04-27T10:50:00"
}
```

## 🗄 DB 테이블

**users**
| 컬럼 | 타입 | 설명 |
|------|------|------|
| firebase_uid | VARCHAR | 기본키, Firebase UID |
| email | VARCHAR | 이메일 (unique) |
| nickname | VARCHAR | 닉네임 |
| created_at | TIMESTAMP | 가입일시 |

**study_records**
| 컬럼 | 타입 | 설명 |
|------|------|------|
| id | BIGINT | 기본키 (auto increment) |
| firebase_uid | VARCHAR | 유저 Firebase UID |
| study_minutes | INT | 공부시간 (분) |
| start_time | TIMESTAMP | 시작 시간 |
| end_time | TIMESTAMP | 종료 시간 |
| created_at | TIMESTAMP | 기록 생성 시간 |

## ✅ 테스트

```bash
./gradlew test
```

| 테스트 | 개수 |
|--------|------|
| AuthService 단위 테스트 | 6개 |
| TimerService 단위 테스트 | 9개 |
| 합계 | 15개 |

## 🌱 로컬 실행 방법

1. Firebase 프로젝트 생성 후 서비스 계정 키 발급
2. `src/main/resources/firebase-adminsdk.json` 에 키 파일 추가
3. 애플리케이션 실행

```bash
./gradlew bootRun
```

4. H2 콘솔 접속: `http://localhost:8080/h2-console`
   - JDBC URL: `jdbc:h2:mem:pokitdb`
   - Username: `sa`