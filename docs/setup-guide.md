# 팜터레스트 설치·실행 가이드

## 0. 사전 준비 (이미 PC에 설치되어 있어야 함)
- **JDK 17+** (개발/검증 환경: Java 21)
- **Apache Tomcat 10.1.x**  → `C:\apache-tomcat-10.1.55`
- **MySQL 8.0** 실행 중 (root 비밀번호 알고 있어야 함)

> 경로/비밀번호가 다르면 아래 파일의 상단 변수만 바꾸면 됩니다.
> - `setup-db.bat` : MySQL 경로, root 비밀번호(PW)
> - `build.bat` / `deploy.bat` : TOMCAT 경로
> - `run.bat` : JAVA_HOME, CATALINA_HOME
> - `src/main/webapp/META-INF/context.xml` : DB url/계정 (기본 farmterest/farmterest123!)

## 1. 데이터베이스 만들기
명령 프롬프트(cmd)를 프로젝트 폴더에서 열고:
```
setup-db.bat
```
→ `farmterest` DB, 전용 계정, 테이블, 샘플 데이터가 생성됩니다.
(root 비밀번호가 1234가 아니면 `setup-db.bat` 의 `set PW=` 를 수정)

## 2. 빌드
```
build.bat
```
→ Java를 컴파일하고 `build/` 에 배포용 웹앱을 조립합니다. (Maven 불필요)

## 3. 배포
```
deploy.bat
```
→ `build/` 를 Tomcat `webapps\farmterest` 로 복사하고, MySQL 드라이버를 Tomcat\lib 에 넣습니다.

## 4. 실행
```
run.bat
```
→ Tomcat이 켜집니다. 브라우저에서:
```
http://localhost:8080/farmterest
```
종료는 `stop.bat`.

## 5. 체험 계정
| 구분 | 아이디 | 비밀번호 |
|---|---|---|
| 소비자(구매이력 보유) | `user1` | `1234` |
| 판매자 | `seller1` | `1234` |

## 6. 둘러볼 포인트 (시연 추천 동선, 2분)
1. 메인 → 자연어 검색 **"정백도 높은 평창 햅쌀"** → AI 추천 패널 + 정확한 결과 (상단 검색창엔 실시간 인기 검색어가 흐름)
2. 상품 상세 → **품질 지표 도넛 차트**(정백도·완전립·식미치·수분, 가운데 수치)
3. `user1` 로그인 → **마이페이지는 비어 있음**(시드 데이터 없음) — 여기서부터 직접 채워가며 시연
4. 로그인 상태로 **검색** → 마이페이지 '최근 검색어'에 즉시 반영 / **장바구니 담기 → 주문** → '주문 내역'·'취향 분석 대시보드'·'맞춤 추천'이 **실시간으로 채워짐**(재고도 차감)
5. `seller1` 로그인 → 판매자 센터에서 상품 등록/수정/삭제

> 마이페이지·대시보드·추천은 미리 박은 시드 데이터가 없어, 시연 중 실제 행동(검색·구매)으로 자연스럽게 쌓입니다.

## 7. 자주 나는 문제
- **드라이버 오류**: `deploy.bat` 이 MySQL 커넥터를 Tomcat\lib 에 넣습니다. 누락 시 `lib\mysql-connector-j-8.4.0.jar` 를 직접 복사.
- **한글 깨짐**: Tomcat 10 기본 UTF-8. context.xml의 url에 `characterEncoding=UTF-8` 포함됨.
- **포트 충돌(8080)**: 다른 Tomcat 종료 후 재실행.
- **인증 플러그인 오류**: context.xml url에 `allowPublicKeyRetrieval=true&useSSL=false` 포함됨(로컬 전용).

## 8. (선택) AI 맞춤검색 고도화 — OpenRouter 연동
키가 없으면 규칙기반으로 동작하고, 키를 넣으면 LLM이 라이브 상품을 근거로 추천·설명을 생성합니다.

1. https://openrouter.ai/keys 에서 API 키 발급 + **계정에 선불 $30만 충전**(자동충전 OFF) → 결제 측 하드 상한.
2. 키 입력 파일(홈 디렉터리, 깃에 안 올라감):
   ```
   파일: %USERPROFILE%\.farmterest\openrouter.properties
   줄:   openrouter.api.key=sk-or-...   ← 여기에 붙여넣고 저장
   ```
3. 저장하면 **재배포 없이 자동 반영**(파일 변경 감지). 검색해 보면 "✦ AI 추천" 패널이 뜹니다.
- 모델: `openrouter.model=auto` → 가장 저렴한 모델 자동 선정.
- 안전장치(2중): 앱이 월 누적 비용을 `ai_usage` 테이블에 기록 → 월 $30의 `자동차단 $25` 도달 시 규칙기반 전환 + OpenRouter 선불 한도. 검색 페이지 하단에 이번 달 사용액이 표시됩니다.
