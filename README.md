# 🌾 팜터레스트 (Farmterest)

> **강원의 진짜를, 숫자로 고른다.**
> 강원도 농수산물 직거래 + 규칙기반 맞춤 추천 · 웹서버 구축 기말 프로젝트

품질지표(정백도·완전립·수분·식미치)를 **숫자로 공개**하고, 구매이력을 반영해
**근거와 함께** 추천하는 산지 직거래 웹 애플리케이션입니다.

## ✨ 핵심 기능
- **AI 맞춤검색** — "정백도 높은 평창 햅쌀 저렴하게" 같은 한글 자연어를 분석해 동적 SQL로 검색
- **품질 투명 공개** — 모든 상품에 정백도·완전립·식미치·수분 지표 + 상세 성분표 게이지
- **근거 있는 추천** — 구매이력 기반 선호 프로필로 점수화, "왜 추천했는지" 문구 표시
- **세션 기반 회원/권한** — 소비자·판매자 분리, 판매자 상품 CRUD
- **장바구니 → 주문(트랜잭션)** — 주문 시 재고 차감, 구매이력 누적
- **반응형 UI** — PC 2단(좌측 필터) / 모바일 아코디언

## 🛠 기술 스택
`Java 21` · `Tomcat 10.1 (Jakarta Servlet 6.0)` · `JSP / JSTL 3.0` · `MySQL 8.0` · `JDBC + DBCP`
**아키텍처: MVC v2 (Front Controller)** · JavaBean(DTO) · DAO · Service · 동적 SQL · DBCP 커넥션풀

## 🚀 빠른 시작
```bat
setup-db.bat   :: DB·스키마·샘플데이터 생성 (MySQL root 필요)
build.bat      :: javac 컴파일 + 웹앱 조립 (Maven 불필요)
deploy.bat     :: Tomcat 에 배포
run.bat        :: Tomcat 기동 → http://localhost:8080/farmterest
```
자세한 내용: [docs/setup-guide.md](docs/setup-guide.md)

체험 계정 — 소비자 `user1` / 판매자 `seller1` (비밀번호 모두 `1234`)

## 📚 문서
- [시스템 구성도](docs/architecture.md) · [파일 구조도](docs/file-tree.md)
- [설치 가이드](docs/setup-guide.md) · [마케팅 전략](docs/marketing.md)
- [설계서](docs/superpowers/specs/2026-06-08-farmterest-design.md)

## 📁 구조 요약
```
controller/  FrontController + Action 16종      (흐름 제어)
model/dto/   JavaBean 7종                        (데이터)
model/dao/   JDBC DAO 4종 (동적SQL·트랜잭션)     (영속)
service/     인증·장바구니·자연어파서·추천엔진    (로직)
WEB-INF/views/  JSP 14종 + 공통 레이아웃          (표현)
WEB-INF/tags/   productCard · categoryArt        (재사용 컴포넌트)
```
