# AningCall Backend API

AningCall 알람 앱의 REST API 백엔드 서버입니다.

## 🏗️ 기술 스택

- **Framework**: Spring Boot 3.3.x
- **Language**: Java 17
- **Database**: MySQL 8.0
- **Cache**: Redis 7.2
- **Build Tool**: Gradle
- **Container**: Docker & Docker Compose
- **CI/CD**: GitHub Actions

## 🚀 빠른 시작

### 개발 환경 실행

```bash
# 1. 저장소 클론
git clone <repository-url>
cd BE-spring

# 2. 로컬 개발 스택 실행 (MySQL, Redis 포함)
docker compose -f docker/compose/local.yml up -d

# 3. 애플리케이션 로그 확인
docker compose -f docker/compose/local.yml logs -f app
```

### 로컬 개발 (IDE에서 실행)

```bash
# 1. 외부 서비스만 실행
docker compose -f docker/compose/local.yml up -d mysql redis

# 2. IDE에서 애플리케이션 실행
./gradlew bootRun --args='--spring.profiles.active=local'
```

## 🐳 Docker 환경

### 개발 환경
- **URL**: `http://localhost:8080/api`
- **Database**: MySQL (localhost:3307)
- **Cache**: Redis (localhost:6379)
- **DB Admin**: phpMyAdmin (localhost:8081)
- **Redis Admin**: Redis Commander (localhost:8082)

### 프로덕션 환경
```bash
# 환경 변수 설정
cp .env.example .env
# .env 파일 수정 후

# 프로덕션 실행
docker compose -f docker/compose/prod.yml up -d
```

## 📡 API 문서

- **Base URL (prod)**: `https://prod.proproject.my/api`
- **Base URL (dev)**: `https://dev.proproject.my/api`
- **Health Check**: `/health`

### 주요 엔드포인트

| 기능 | 엔드포인트 | 메서드 |
|------|------------|--------|
| 회원가입 | `/auth/register` | POST |
| 로그인 | `/auth/login` | POST |
| 사용자 정보 | `/users/me` | GET |
| 포인트 내역 | `/points/history` | GET |
| 통화 기록 | `/call-logs` | POST/GET |
| 미션 결과 | `/mission-results` | POST/GET |
| 통계 조회 | `/statistics/overview` | GET |

자세한 API 명세는 [docs/prd-backend/api-specification.mdc](docs/prd-backend/api-specification.mdc) 참조

## 🏗️ 프로젝트 구조

```
src/
├── main/
│   ├── java/com/bespring/
│   │   ├── config/          # 설정 클래스
│   │   ├── controller/      # REST 컨트롤러
│   │   ├── service/         # 비즈니스 로직
│   │   ├── repository/      # 데이터 액세스
│   │   ├── entity/          # JPA 엔티티
│   │   ├── dto/            # 데이터 전송 객체
│   │   └── security/        # 보안 설정
│   └── resources/
│       └── application.yml  # 설정 파일
├── test/                   # 테스트 코드
docker/                     # Docker 설정
├── mysql/                  # MySQL 초기화 스크립트
├── nginx/                  # Nginx 설정
└── redis/                  # Redis 설정
.github/workflows/          # GitHub Actions CI/CD
docs/                       # 프로젝트 문서
```

## 🔧 개발 환경 설정

### 필수 요구사항
- Java 17
- Docker & Docker Compose
- Git

### IDE 설정 (IntelliJ IDEA)
1. Project SDK: Java 17
2. Build Tool: Gradle
3. Annotation Processing 활성화

### 환경별 프로파일
- `local`: H2 DB, 로컬 개발용
- `dev`: MySQL + Redis, 개발 서버용
- `test`: H2 DB, 테스트용
- `prod`: MySQL + Redis, 운영 서버용

## 🧪 테스트

```bash
# 단위 테스트 실행
./gradlew test

# 통합 테스트 실행
./gradlew integrationTest

# 테스트 커버리지 리포트
./gradlew jacocoTestReport
```

## 📦 빌드 & 배포

### GitHub Actions CI/CD

1. **개발 배포**: `develop` 브랜치 푸시 시 자동 배포
2. **프로덕션 배포**: `main` 브랜치 푸시 시 자동 배포

### 수동 배포

```bash
# 도커 이미지 빌드
docker build -t aningcall-backend .

# 프로덕션 배포
docker compose -f docker/compose/prod.yml up -d
```

## 🔒 보안

- JWT 기반 인증
- BCrypt 패스워드 암호화
- CORS 설정
- Rate Limiting (Nginx)
- SSL/TLS 적용

## 📊 모니터링

- **Health Check**: `/actuator/health`
- **Metrics**: `/actuator/metrics`
- **Prometheus**: `/actuator/prometheus`

## 🛠️ 개발 도구

### 데이터베이스 관리
- **개발환경**: phpMyAdmin (http://localhost:8081)
  - ID: `root` / PW: `devpassword`

### 캐시 관리
- **개발환경**: Redis Commander (http://localhost:8082)

## 📝 기여 가이드

1. 기능 브랜치 생성
2. 커밋 메시지 규칙 준수
3. Pull Request 작성
4. 코드 리뷰 및 테스트 통과 후 병합

## 📄 라이선스

이 프로젝트는 MIT 라이선스를 따릅니다.

## 🆘 문제 해결

### 자주 발생하는 문제

1. **포트 충돌**
   ```bash
   # 사용 중인 포트 확인
   lsof -i :8080
   lsof -i :3307
   ```

2. **Docker 권한 문제**
   ```bash
   # Docker 재시작
   docker compose -f docker/compose/prod.yml down
   docker compose -f docker/compose/prod.yml up -d
   ```

3. **데이터베이스 연결 실패**
   ```bash
   # MySQL 컨테이너 로그 확인(로컬 개발)
   docker compose -f docker/compose/local.yml logs mysql
   ```

더 많은 정보는 [troubleshooting guide](docs/troubleshooting.md) 참조
