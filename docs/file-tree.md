# 팜터레스트 파일 구조도 (Tree)

```
farmterest/
├── setup-db.bat            # ① DB/스키마/시드 생성 (root 권한)
├── build.bat               # ② javac 컴파일 + 웹리소스 조립 → build/
├── deploy.bat              # ③ build/ → Tomcat webapps 배포 (+ MySQL 드라이버 배치)
├── run.bat / stop.bat      # ④ Tomcat 기동/종료
│
├── lib/                    # 런타임 라이브러리 (1회 다운로드)
│   ├── mysql-connector-j-8.4.0.jar
│   ├── jakarta.servlet.jsp.jstl-api-3.0.0.jar
│   └── jakarta.servlet.jsp.jstl-3.0.1.jar
│
├── sql/
│   ├── setup_user.sql      # DB·전용계정 생성
│   ├── schema.sql          # 테이블 5종 (정규화·FK·인덱스)
│   └── seed.sql            # 강원 상품 22 + 회원 4 + 주문이력
│
├── docs/                   # 포트폴리오 문서
│   ├── architecture.md     # 시스템 구성도 (이 프로젝트의 설계 설명)
│   ├── marketing.md        # 마케팅 전략 한 장
│   ├── file-tree.md        # (이 문서)
│   ├── setup-guide.md      # 설치·실행 가이드
│   ├── superpowers/specs/  # 설계서(요구사항 정리)
│   └── internal/           # 개발용 뷰 작성 계약
│
└── src/main/
    ├── java/com/farmterest/
    │   ├── controller/
    │   │   ├── FrontController.java     # 단일 진입점(*.do) — commandMap 분기
    │   │   ├── Action.java              # 요청 처리 인터페이스
    │   │   ├── ActionForward.java       # 뷰 이동 정보(forward/redirect)
    │   │   └── action/                  # 기능별 Action 16종
    │   │       ├── MainAction · LoginAction · LogoutAction · JoinAction
    │   │       ├── ProductListAction · ProductDetailAction · SearchAction
    │   │       ├── SellerProducts/Form/Save/DeleteAction   (판매자 CRUD)
    │   │       ├── CartAdd/View/RemoveAction · OrderAction
    │   │       └── MyPageAction
    │   ├── model/
    │   │   ├── dto/   # JavaBean: Member · Product · Order · OrderItem
    │   │   │          #           Cart Item · Recommendation · PreferenceProfile
    │   │   └── dao/   # JDBC: MemberDAO · ProductDAO(동적SQL) · OrderDAO(트랜잭션) · SearchLogDAO
    │   ├── service/   # AuthService · CartService(세션) · QueryParser(자연어) · RecommendationService
    │   └── util/      # DBManager(DBCP/JNDI) · Params · SearchCriteria
    │
    └── webapp/
        ├── index.jsp                    # main.do 로 포워딩
        ├── META-INF/context.xml         # DBCP 커넥션풀 Resource
        ├── css/style.css                # 디자인 시스템(반응형)
        ├── images/prod-{1..22}.svg      # 품목별 홈쇼핑 스타일 상품 이미지(로컬 합성 SVG)
        ├── js/app.js                    # 필터 아코디언·수량·게이지
        └── WEB-INF/
            ├── web.xml                  # 서블릿 매핑 + resource-ref
            ├── tags/                    # 재사용 태그
            │   ├── productCard.tag      # 상품 카드(전 페이지 공통)
            │   └── categoryArt.tag      # 품목 SVG 비주얼 + 원산지 스탬프
            └── views/                   # 모든 뷰(직접 접근 차단)
                ├── common/header.jsp · footer.jsp
                ├── main · productList · productDetail · searchResult
                ├── login · join · cart · orderDone · mypage
                ├── sellerProducts · sellerProductForm
                └── error.jsp
```

## 빌드 산출물
- `build/` : `build.bat` 이 생성하는 배포 가능한 웹앱(WEB-INF/classes 컴파일 결과 포함). 소스 아님.
