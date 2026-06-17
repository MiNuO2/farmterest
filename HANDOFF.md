# HANDOFF — 팜터레스트

작성: 2026-06-08 · 상태: **로컬 구동 + 전 기능 검증 완료**

## 지금 상태
- 웹앱 완성, Tomcat 10에 배포되어 `http://localhost:8080/farmterest` 에서 동작.
- 14개 화면 + 16개 Action + 5개 테이블, 전부 브라우저로 종단 검증함.
- DB는 시드 원본 상태(상품 22 · 회원 4 · 주문이력 3)로 리셋해 둠.

## 검증한 것 (실제 동작 확인)
- 메인/목록/상세/검색/로그인/회원가입/장바구니/주문완료/마이페이지/판매자센터/등록폼 → 모두 HTTP 200, 에러 없음.
- **AI 자연어검색**: "정백도 높은 평창 햅쌀" → 정확히 해당 상품 1건.
- **맞춤추천**: user1 로그인 → 마이페이지에 선호도(쌀/홍천/정백도93%) + 근거 붙은 추천.
- **주문 트랜잭션**: 주문 시 DB 저장 + 재고 차감 확인.
- **판매자 CRUD**: 상품 등록 → DB insert 확인.
- **상품 이미지**: 품목별 홈쇼핑 스타일 SVG 22장(`webapp/images/prod-{id}.svg`) — 카드·상세에 표시. 이미지 없는 상품은 SVG 아이콘으로 자동 폴백. seed.sql 끝의 UPDATE가 image_url 연결(재현 가능).
- **OpenRouter(LLM) AI검색**: 키 있으면 LLM이 라이브 상품 근거로 추천·설명 생성, 없으면 규칙기반 폴백(검증됨). 키 위치: `%USERPROFILE%\.farmterest\openrouter.properties` 의 `openrouter.api.key=`. 모델 auto(최저가 자동선정). 안전장치: `ai_usage` 월 예산($30, 자동차단 $25) + OpenRouter 선불한도. 컴파일에 `lib/gson-2.11.0.jar` 필요.

## 다음 할 일 (사용자 선택)
1. **GitHub 업로드** — 새 리포 생성 후 푸시 (현재는 로컬 전용, git 미설정).
   - `.gitignore` 에 `build/` 추가 권장(빌드 산출물). `lib/*.jar` 는 포함하거나 받는 법 안내.
2. **시연 영상(2분)** — docs/setup-guide.md §6 동선 참고.
3. (선택) 비밀번호 해시, 페이지네이션, 상품 이미지 업로드 등 고도화.

## 알아둘 점 (함정)
- `.bat` 파일은 **ASCII 전용** (한글 넣으면 cmd에서 깨짐). 메시지는 영문.
- JSP에 **아스트랄 이모지(🌾 등) 금지** → 컴파일 시 "??"로 깨짐. 아이콘은 `categoryArt.tag` 의 SVG 사용.
- EL에서 `empty` 는 예약어 → `profile.empty` 대신 `profile.isEmpty()` 호출.
- Tomcat 10 = **jakarta.servlet** (javax 아님), JSTL URI 는 `jakarta.tags.core` 등.
- MySQL 드라이버는 `deploy.bat` 이 Tomcat\lib 에 배치(컨테이너 DBCP가 로드).
- **JSP 조각 인코딩**: `<%@ include %>` 로 끼우는 조각(footer 등)에도 `<%@ page pageEncoding="UTF-8" %>` 가 있어야 한글이 안 깨짐(없으면 mojibake).
- **CSS/JS 캐시**: 스타일 바꿔도 브라우저가 옛것을 쓰면 `header.jsp`/`footer.jsp` 의 `?v=...` 버전 숫자를 올려 강제 새로고침.
- **레이아웃**: body 가 flex column(스티키 푸터)이라 본문이 짧아도 푸터가 화면 아래에 붙음.

## 환경값
- 프로젝트: `C:\Users\yangi\farmterest`
- JDK: `C:\Program Files\Java\jdk-21.0.11` · Tomcat: `C:\apache-tomcat-10.1.55`
- DB: MySQL 8.0, 스키마 `farmterest`, 앱계정 `farmterest`/`farmterest123!`, root 비번 `1234`
