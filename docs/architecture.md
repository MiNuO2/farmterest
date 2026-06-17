# 팜터레스트 시스템 구성도 (Architecture)

> MVC v2 (Front Controller) · JSP · JavaBean · JDBC/DBCP · MySQL
> 강원 농수산물 직거래 + 규칙기반 맞춤 추천

## 1. 전체 구성도

```
┌────────────┐   *.do 요청    ┌──────────────────────────────────────────┐
│  브라우저   │ ────────────▶ │            FrontController (Servlet)        │
│ (HTML/CSS/ │               │   - UTF-8 인코딩 설정                        │
│   JS)      │ ◀──────────── │   - commandMap 으로 URL → Action 분기       │
└────────────┘   HTML 응답    └───────────────┬──────────────────────────┘
                                              │ Action.execute()
                                              ▼
                              ┌──────────────────────────────┐
                              │   Action (16종, 요청 1:1)      │
                              │  Login/Product/Search/Cart…    │
                              └───────┬───────────────┬───────┘
                                      │               │
                          ┌───────────▼──┐     ┌──────▼──────────────┐
                          │   Service     │     │   DTO (JavaBean)     │
                          │ Auth/Cart/    │     │ Member/Product/Order │
                          │ QueryParser/  │     │ /Recommendation …    │
                          │ Recommendation│     └──────────────────────┘
                          └───────┬───────┘
                                  │
                          ┌───────▼───────┐    JDBC      ┌──────────────┐
                          │   DAO (4종)    │ ──────────▶ │ DBCP 커넥션풀 │ ─▶ MySQL
                          │ Member/Product│ ◀────────── │ (context.xml) │
                          │ /Order/Search │             └──────────────┘
                          └───────────────┘
                                  │ ActionForward(뷰경로)
                                  ▼
                          ┌───────────────┐
                          │  JSP + JSTL    │  ← 모든 뷰는 WEB-INF/views (직접접근 차단)
                          │  + 재사용 태그  │     재사용 태그: productCard, categoryArt
                          └───────────────┘
```

## 2. 계층별 책임

| 계층 | 위치 | 책임 |
|---|---|---|
| **Controller** | `controller/FrontController` + `controller/action/*` | 단일 진입점에서 요청을 받아 Action 으로 분기, 뷰로 포워딩 |
| **Service** | `service/*` | 비즈니스 로직 (인증, 장바구니, 자연어 파싱, 추천 점수화) |
| **DAO** | `model/dao/*` | JDBC로 DB 접근. ProductDAO.search()는 **동적 SQL** |
| **DTO(JavaBean)** | `model/dto/*` | private 필드 + getter/setter 데이터 객체 |
| **View** | `WEB-INF/views/*.jsp` | JSTL/EL 로 데이터 표현. 컨트롤러 경유만 허용 |
| **Util** | `util/*` | DBManager(DBCP 조회), Params(파라미터), SearchCriteria |

## 3. 핵심 요청 흐름 예시 — "정백도 높은 평창 햅쌀" 검색

1. 브라우저 → `GET /search.do?q=정백도 높은 평창 햅쌀`
2. `FrontController` → `SearchAction`
3. `QueryParser.parse()` → SearchCriteria(category=쌀, region=평창, minPolishedRate=90, sort=품질순)
4. `ProductDAO.search()` → 채워진 조건만 **동적 SQL** (WHERE/ORDER BY) 조립해 질의
5. 로그인 회원이면 `RecommendationService` 가 구매이력 프로필로 **재정렬 + 근거 생성**
6. `SearchLogDAO.insert()` 로 검색 기록 저장 (세션/추천 데이터)
7. `ActionForward` → `searchResult.jsp` → "AI가 이렇게 이해했어요" + 결과 렌더링

## 4. 강의 기술 요소 매핑

| 강의 요소 | 구현 위치 |
|---|---|
| 웹서버/WAS, 한글 처리 | Tomcat 10, `FrontController.setCharacterEncoding("UTF-8")`, JSP `charset=UTF-8` |
| HTML5/CSS3/반응형 | `css/style.css` (Fluid grid + @media), 시맨틱 태그 |
| 자바스크립트 DOM | `js/app.js` (필터 아코디언, 수량 스테퍼, 게이지) |
| JSP 내장객체 | request(attribute), **session(loginMember/cart)**, response(redirect) |
| 쿠키/세션 | 세션 로그인 상태 유지 + 역할 기반 접근제어, 세션 장바구니 |
| JavaBean | `model/dto/*` + JSP에서 EL/`ui:` 태그로 사용 |
| JDBC | `model/dao/*` (Connection/PreparedStatement/ResultSet) |
| **DBCP 커넥션풀** | `META-INF/context.xml` Resource + `web.xml` resource-ref + `DBManager` JNDI 조회 |
| **MVC v2** | Front Controller + Action 인터페이스 + ActionForward |

## 5. DB ER 개요

```
member 1 ──< product (seller_id)
member 1 ──< orders 1 ──< order_item >── 1 product
member 1 ──< search_log
```
- product 에 품질지표(polished_rate/whole_grain_rate/moisture/taste_score) 보유 — 필터·추천의 핵심 데이터.

## 6. 추천 엔진 (규칙기반, 100% 로컬)

```
구매이력(order_item→product)
      │  buildProfile()
      ▼
PreferenceProfile (선호 품목/지역/평균 정백도·식미치)
      │  rank(): 가중 점수화
      ▼  품목+40 / 지역+20 / 정백도근접+25 / 식미치+15 (+재고)
Recommendation (상품 + 점수 + "근거 문구")  ← 설명가능성
```
- 외부 LLM·인터넷 불필요. 비로그인은 인기순, 로그인은 맞춤 점수순으로 자동 전환.
