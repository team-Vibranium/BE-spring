# JWT Secret Key Management Guide

## 🔐 JWT 시크릿 키 보안 관리 가이드

### ⚠️ 중요한 보안 원칙

1. **절대로 코드에 하드코딩하지 마세요**
2. **Git에 커밋하지 마세요** (.env 파일은 .gitignore에 포함)
3. **환경별로 다른 키를 사용하세요** (개발/스테이징/운영)
4. **정기적으로 키를 교체하세요** (6-12개월마다)

---

## 🔧 키 생성 방법

### Method 1: PowerShell (Windows)
```powershell
# 보안 강화된 랜덤 키 생성
Add-Type -AssemblyName System.Security
$rng = New-Object System.Security.Cryptography.RNGCryptoServiceProvider
$bytes = New-Object byte[] 64
$rng.GetBytes($bytes)
$JWT_SECRET = [Convert]::ToBase64String($bytes)
Write-Host "JWT_SECRET=`"$JWT_SECRET`""
```

### Method 2: OpenSSL (Linux/Mac)
```bash
# OpenSSL로 안전한 키 생성
openssl rand -base64 64
```

### Method 3: Node.js
```javascript
// Node.js crypto 모듈 사용
const crypto = require('crypto');
const secret = crypto.randomBytes(64).toString('base64');
console.log('JWT_SECRET="' + secret + '"');
```

### Method 4: Python
```python
# Python secrets 모듈 사용
import secrets
jwt_secret = secrets.token_urlsafe(64)
print(f'JWT_SECRET="{jwt_secret}"')
```

---

## 📋 환경별 키 설정

### 개발 환경 (.env)
```bash
# .env 파일 (로컬 개발용)
JWT_SECRET="개발용_보안키_여기에_입력"
JWT_EXPIRATION=86400000
```

### 스테이징 환경
```bash
# 환경변수로 설정
export JWT_SECRET="스테이징용_보안키_여기에_입력"
export JWT_EXPIRATION=86400000
```

### 운영 환경
```bash
# 운영 서버 환경변수
export JWT_SECRET="운영용_최고보안_키_여기에_입력"
export JWT_EXPIRATION=3600000  # 1시간 (운영에서는 짧게)
```

---

## 🏗️ 클라우드 환경 설정

### AWS (Elastic Beanstalk/EC2)
```bash
# AWS CLI로 환경변수 설정
aws elasticbeanstalk put-configuration-template \
  --application-name myapp \
  --template-name production \
  --option-settings Namespace=aws:elasticbeanstalk:application:environment,OptionName=JWT_SECRET,Value="your-secret-key"
```

### Azure (App Service)
```bash
# Azure CLI로 환경변수 설정
az webapp config appsettings set \
  --resource-group myResourceGroup \
  --name myAppName \
  --settings JWT_SECRET="your-secret-key"
```

### Docker
```dockerfile
# Dockerfile
ENV JWT_SECRET=""
# 실행 시: docker run -e JWT_SECRET="your-key" myapp
```

### Docker Compose
```yaml
# docker/compose/prod.yml
services:
  app:
    environment:
      - JWT_SECRET=${JWT_SECRET}
```

---

## 🔒 보안 저장소 사용 (권장)

### AWS Secrets Manager
```bash
# 시크릿 생성
aws secretsmanager create-secret \
  --name "myapp/jwt-secret" \
  --secret-string "your-secret-key"

# 애플리케이션에서 불러오기
aws secretsmanager get-secret-value \
  --secret-id "myapp/jwt-secret" \
  --query SecretString --output text
```

### Azure Key Vault
```bash
# Key Vault에 저장
az keyvault secret set \
  --vault-name myKeyVault \
  --name jwt-secret \
  --value "your-secret-key"
```

### HashiCorp Vault
```bash
# Vault에 저장
vault kv put secret/myapp jwt_secret="your-secret-key"
```

---

## 🔄 키 로테이션 (교체) 전략

### 1. 점진적 교체 (Zero-downtime)
```bash
# 1단계: 새 키 추가 (기존 키와 병행)
JWT_SECRET_OLD="old-key"
JWT_SECRET_NEW="new-key"

# 2단계: 새 키로만 토큰 생성, 양쪽 키로 검증
# 3단계: 충분한 시간 후 새 키로만 검증
JWT_SECRET="new-key"
```

### 2. 키 교체 체크리스트
- [ ] 새 보안 키 생성
- [ ] 스테이징 환경에서 테스트
- [ ] 운영 환경에 새 키 적용
- [ ] 기존 사용자 토큰 만료 대기
- [ ] 구 키 제거
- [ ] 백업 저장소에서 구 키 삭제

---

## 📊 키 길이 및 보안 요구사항

### HMAC SHA-256 기준
- **최소 길이**: 256 bits (32 bytes)
- **권장 길이**: 512 bits (64 bytes)
- **Base64 인코딩 시**: 최소 44자, 권장 88자

### 보안 체크포인트
```bash
# 키 길이 확인
echo -n "your-jwt-secret" | wc -c  # 바이트 수 확인

# Base64 디코딩 후 길이 확인
echo "your-base64-key" | base64 -d | wc -c
```

---

## ⚡ 스크립트 사용법

### Windows PowerShell
```powershell
# 키 생성 스크립트 실행
.\scripts\generate-jwt-secret.ps1
```

### Linux/Mac Bash
```bash
# 키 생성 스크립트 실행
chmod +x scripts/generate-jwt-secret.sh
./scripts/generate-jwt-secret.sh
```

---

## 🚨 보안 사고 대응

### JWT 키 유출 시 대응
1. **즉시 새 키 생성 및 배포**
2. **모든 기존 토큰 무효화**
3. **사용자 재로그인 강제**
4. **로그 분석으로 영향 범위 파악**
5. **보안팀 및 관련자 보고**

### 모니터링 포인트
- 비정상적인 JWT 토큰 생성 패턴
- 잘못된 서명으로 인한 인증 실패 급증
- 예상보다 많은 토큰 검증 요청

---

## 📖 참고 자료

- [RFC 7519 - JSON Web Token](https://tools.ietf.org/html/rfc7519)
- [JWT.io - JWT Debugger](https://jwt.io/)
- [OWASP JWT Security Guide](https://cheatsheetseries.owasp.org/cheatsheets/JSON_Web_Token_for_Java_Cheat_Sheet.html)
