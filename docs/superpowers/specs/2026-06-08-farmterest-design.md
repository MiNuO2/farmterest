# 팜터레스트(Farmterest) 설계서

> 강원도 농수산물 직거래 + 규칙기반 맞춤형 상품추천 · MVC v2 웹서버 구축
> 작성일: 2026-06-08 · 대상: 웹서버 구축 기말 프로젝트 제출용

---

## 1. 개요 / 목표

| 항목 | 내용 |
|---|---|
| 서비스명 | 팜터레스트 (Farmterest = Farm + Interest) |
| 한 줄 정의 | "강원 산지에서 바로, **품질 지표까지 투명하게**" |
| 핵심 차별점 | 정백도·완전립·수분·식미치 등 **객관적 품질 지표 공개** + **이유까지 설명하는 맞춤 추천** |
| 기술 목표 | MVC v2(Front Controller) · JSP · JavaBean · JDBC/DBCP · 반응형 UI를 정석대로 구현 |
| 구동 환경 | Java 21 · Tomcat 10.1(jakarta.servlet 6.0) · MySQL 8.0 · **100% 로컬** |

## 2. 평가 기준 충족 매핑

| 평가 항목 (20%씩) | 충족 전략 |
|---|---|
| 창의성 | 세션 구매이력 기반 **맞춤 추천 + 추천 근거 설명**, 강원 품질지표 투명 공개 |
| 기술 적용도 | MVC v2 Front Controller · JavaBean DTO · DAO · **DBCP 커넥션풀** · **동적 SQL** |
| UI 설계 | HTML5 시맨틱 · CSS3 · 반응형(Fluid grid + @media) · 모바일 아코디언 필터 |
| 완성도 | 회원/상품/필터/검색/판매자CRUD/장바구니/주문/마이페이지 풀세트 + 실제 구동 |
| 발표/영상 | 동작하는 사이트로 시연(2분) + 구조도/마케팅 포함 PPT(5분) |

## 3. 시스템 아키텍처 — MVC v2 (Front Controller)

```
[브라우저] ──*.do──▶ FrontController (jakarta HttpServlet)
                          │  요청 URL → Action 매핑(commandMap)
                          ▼
                     Action.execute()  ──▶  Service 계층
                          │                   (Recommendation/Auth/Cart)
                          │                        │
                          │                        ▼
                          │                   DAO ──(JDBC)──▶ [DBCP 풀] ──▶ MySQL
                          ▼
                     ActionForward (뷰 경로 + redirect 여부)
                          │
                          ▼
                     JSP + JSTL/EL  ──▶  HTML5 응답
```

- **단일 진입점**: 모든 동적 요청은 `*.do` → `FrontController`가 받아 분기. 강의의 "컨트롤러가 모든 흐름 제어(MVC v2)" 정확 구현.
- **JSP 은닉**: 모든 뷰 JSP는 `WEB-INF/views/` 하위 → URL 직접 접근 불가, 반드시 컨트롤러 경유.
- **계층 분리**: Controller(흐름) / Service(로직) / DAO(영속) / DTO(데이터) / View(표현) 5분리.

### 3.1 핵심 클래스 계약(Contract)

```
controller/
  FrontController.java   // *.do 매핑, commandMap으로 Action 분기, 한글 인코딩 처리
  Action.java            // interface: ActionForward execute(req, res) throws Exception
  ActionForward.java     // String path; boolean redirect;
  action/...Action.java  // 기능별 구현체

model/dto/  (JavaBean: private 필드 + getter/setter + 기본생성자)
  MemberDTO  ProductDTO  OrderDTO  OrderItemDTO  CartItem  SearchResult  Recommendation

model/dao/  (JDBC, DBCP 커넥션 사용)
  MemberDAO  ProductDAO  OrderDAO  SearchLogDAO

service/
  AuthService            // 로그인/회원가입/권한
  CartService            // 세션 장바구니
  QueryParser            // 한글 자연어 → 검색 필터(SearchCriteria)
  RecommendationService  // 구매이력 기반 점수화 + 근거 생성

util/
  DBManager              // JNDI(java:comp/env/jdbc/farmterest) DataSource 조회, getConnection()
  SearchCriteria         // 동적 SQL 조건 컨테이너
```

## 4. 데이터 모델 (MySQL, 정규화 · FK · 인덱스)

```sql
member(member_id PK, login_id UNIQUE, password, name,
       role ENUM('CONSUMER','SELLER'), region, created_at)

product(product_id PK, seller_id FK->member, name, category, region,
        price INT, stock INT, image_url, description,
        polished_rate INT,      -- 정백도(%)  (쌀/잡곡)
        whole_grain_rate INT,   -- 완전립 비율(%)
        moisture DECIMAL(4,1),  -- 수분함량(%)
        taste_score INT,        -- 식미치(점)
        created_at)

orders(order_id PK, member_id FK->member, ordered_at,
       total_price INT, status ENUM('PAID','SHIPPING','DONE','CANCEL'))

order_item(order_item_id PK, order_id FK->orders, product_id FK->product,
           qty INT, unit_price INT)

search_log(log_id PK, member_id FK->member NULL, query_text, searched_at)
```

- 품질 지표 컬럼은 쌀/잡곡 외 품목엔 NULL 허용. 필터·추천 시 존재할 때만 반영.
- 인덱스: product(category), product(region), product(seller_id), order_item(product_id), search_log(member_id).
- 시드: 강원 시군(춘천·강릉·평창·정선·양양 등) × 품목(쌀·잡곡·감자·곤드레·황태·오징어 등) 약 24개 상품 + 판매자/소비자 계정 + 샘플 주문이력.

## 5. 규칙기반 맞춤 추천 엔진 (핵심 차별점)

100% 로컬 · 외부 API 불필요. 두 부품으로 구성.

### 5.1 QueryParser — 한글 자연어 → SearchCriteria
- 키워드 사전 매핑:
  - **지역**: "강릉/평창/정선…" → region 필터
  - **품목**: "쌀/햅쌀/현미/잡곡/감자/곤드레/황태/오징어…" → category 필터(동의어 포함)
  - **정렬·품질어**: "높은/좋은/프리미엄" → 품질 desc, "싼/저렴/가성비" → price asc, "신선/햇/신상" → 최신순
  - **품질지표어**: "정백도/완전립/수분/식미치" → 해당 지표 최소 기준 + 정렬
- 결과 → `SearchCriteria`(category, region, priceMin/Max, 각 품질 최소값, sort)

### 5.2 동적 SQL (ProductDAO.search)
- `SearchCriteria`의 채워진 조건만 `WHERE` / `ORDER BY`로 조립 → 가변 조건 질의. (강의가 강조한 "동적 쿼리" 직접 시연)

### 5.3 RecommendationService — 맞춤 점수화 + 근거
- 입력: 세션 회원의 구매이력(자주 산 category/region, 선호 품질대 평균), (선택)검색 결과 후보
- 각 후보 상품 점수 = 가중합:
  - 품목 일치(+), 지역 일치(+), 선호 품질대 근접(+), 재고/최신(+), 검색어 관련(+)
- 상위 N개 반환 + **근거 문구** 생성: 예) "회원님이 자주 구매한 '정백도 높은 강릉 쌀'과 유사"
- 비로그인 → 인기/신상 기준. 로그인 → 맞춤 점수 기준(세션 분기).

## 6. 기능 목록 & 요청 흐름 (풀세트)

| 기능 | URL(.do) | Action | 권한 |
|---|---|---|---|
| 메인 | `index.jsp`/`main.do` | MainAction | 전체 |
| 회원가입 | `join.do` | JoinAction | 전체 |
| 로그인/로그아웃 | `login.do`/`logout.do` | Login/LogoutAction | 전체 |
| 상품목록+필터 | `productList.do` | ProductListAction | 전체 |
| AI 맞춤검색 | `search.do` | SearchAction | 전체 |
| 상품상세 | `productDetail.do` | ProductDetailAction | 전체 |
| 상품등록/수정/삭제 | `product*.do` | Seller*Action | **SELLER** |
| 장바구니 담기/보기 | `cartAdd.do`/`cart.do` | Cart*Action | CONSUMER |
| 주문 | `order.do` | OrderAction | CONSUMER |
| 마이페이지 | `mypage.do` | MyPageAction | 로그인 |

- **세션**: 로그인 시 `session.setAttribute("loginMember", MemberDTO)`. Action에서 role 검사로 접근 제어. 장바구니는 세션 보관 → 주문 시 DB 영속화.

## 7. UI / 디자인

- HTML5 시맨틱(`header/nav/section/article/footer`) + CSS3.
- 반응형: Fluid grid + `@media` (PC=좌측 필터 고정 2단, 모바일=1단 + 필터 아코디언/바텀시트).
- 강원 자연 톤(딥그린·우드·크림) 팔레트, 카드형 상품 그리드, 품질지표 배지, 추천근거 칩.
- 바닐라 JS: 필터 아코디언 토글, 가격/지표 슬라이더, 장바구니 수량.

## 8. 마케팅 한 장 (간단명료)

- **포지셔닝**: "품질을 숫자로 증명하는 강원 산지 직거래"
- **타깃**: 품질 따지는 소비자 ↔ 소량·고품질 강원 생산자
- **차별점**: 객관 품질지표 공개 + 근거 설명형 맞춤 추천
- **채널**: 지역 농협/로컬푸드 제휴 · 산지 스토리 SNS · 후기 기반 신뢰
- **슬로건**: "강원의 진짜를, 숫자로 고른다."

## 9. 파일 트리

```
farmterest/
├─ build.bat / deploy.bat / run.bat / setup-db.bat
├─ lib/        mysql-connector-j · jakarta.jstl(api+impl)
├─ sql/        schema.sql · seed.sql · setup_user.sql
├─ docs/       architecture.md · file-tree.md · marketing.md · setup-guide.md
└─ src/main/
   ├─ java/com/farmterest/{controller,controller/action,model/dto,model/dao,service,util}
   └─ webapp/
      ├─ WEB-INF/web.xml · views/*.jsp
      ├─ META-INF/context.xml   (DBCP Resource)
      └─ css/ · js/ · images/ · index.jsp
```

## 10. 빌드 · 실행 (Maven 없이)

1. `setup-db.bat` — root/1234로 `sql/*.sql` 실행 → DB·계정·스키마·시드 생성
2. `build.bat` — `javac`로 `src/main/java` 컴파일 → `build/WEB-INF/classes`, webapp 리소스+lib jar 복사
3. `deploy.bat` — `build/` → `C:\apache-tomcat-10.1.55\webapps\farmterest`
4. `run.bat` — Tomcat 기동 → `http://localhost:8080/farmterest`

- 컴파일 classpath = Tomcat `servlet-api.jar` (DAO는 표준 `java.sql`만 사용, 커넥터는 런타임 WEB-INF/lib).
- DBCP: `META-INF/context.xml`의 `<Resource jdbc/farmterest>` + `web.xml`의 `resource-ref`.

## 11. 구현 계획 (단계)

- **P0 기반**: schema/seed/setup, web.xml, context.xml, build/deploy/run 스크립트, DBManager, Action/ActionForward/FrontController, 공통 레이아웃 JSP + CSS 골격
- **P1 인증 슬라이스**: MemberDTO, MemberDAO, AuthService, Join/Login/Logout Action + JSP (수직 슬라이스 템플릿 확립)
- **P2 상품/필터**: ProductDTO, ProductDAO(동적 SQL), ProductList/Detail Action + JSP + 반응형 필터
- **P3 추천/검색**: SearchCriteria, QueryParser, RecommendationService, SearchAction + 결과 JSP(근거 노출)
- **P4 판매자 CRUD**: Seller 상품 등록/수정/삭제 Action + JSP (권한 검사)
- **P5 장바구니/주문/마이페이지**: CartService, OrderDAO, Cart/Order/MyPage Action + JSP
- **P6 마감**: 디자인 다듬기, 문서 4종, 브라우저 시연 검증

## 12. 비범위 (YAGNI)

- 결제 PG 연동 없음(주문=DB 기록까지). 배송추적·리뷰·실시간 채팅 없음. 외부 LLM 없음(규칙기반).
