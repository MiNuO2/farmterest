-- 팜터레스트 스키마 (farmterest DB 안에서 실행)
SET NAMES utf8mb4;

DROP TABLE IF EXISTS ai_usage;
DROP TABLE IF EXISTS order_item;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS search_log;
DROP TABLE IF EXISTS product;
DROP TABLE IF EXISTS member;

-- 회원: 소비자/판매자 권한 구분
CREATE TABLE member (
  member_id   INT AUTO_INCREMENT PRIMARY KEY,
  login_id    VARCHAR(50)  NOT NULL UNIQUE,
  password    VARCHAR(100) NOT NULL,
  name        VARCHAR(50)  NOT NULL,
  role        ENUM('CONSUMER','SELLER') NOT NULL DEFAULT 'CONSUMER',
  region      VARCHAR(50),
  created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 상품: 강원 품질지표(정백도/완전립/수분/식미치) 포함
CREATE TABLE product (
  product_id       INT AUTO_INCREMENT PRIMARY KEY,
  seller_id        INT NOT NULL,
  name             VARCHAR(100) NOT NULL,
  category         VARCHAR(30)  NOT NULL,
  region           VARCHAR(30)  NOT NULL,
  price            INT NOT NULL,
  stock            INT NOT NULL DEFAULT 0,
  image_url        VARCHAR(255),
  description      TEXT,
  polished_rate    INT,            -- 정백도(%)
  whole_grain_rate INT,            -- 완전립 비율(%)
  moisture         DECIMAL(4,1),   -- 수분함량(%)
  taste_score      INT,            -- 식미치(점)
  created_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_product_seller FOREIGN KEY (seller_id) REFERENCES member(member_id),
  INDEX idx_product_category (category),
  INDEX idx_product_region (region),
  INDEX idx_product_seller (seller_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 주문 / 주문상세
CREATE TABLE orders (
  order_id    INT AUTO_INCREMENT PRIMARY KEY,
  member_id   INT NOT NULL,
  ordered_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  total_price INT NOT NULL,
  status      ENUM('PAID','SHIPPING','DONE','CANCEL') NOT NULL DEFAULT 'PAID',
  CONSTRAINT fk_orders_member FOREIGN KEY (member_id) REFERENCES member(member_id),
  INDEX idx_orders_member (member_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE order_item (
  order_item_id INT AUTO_INCREMENT PRIMARY KEY,
  order_id      INT NOT NULL,
  product_id    INT NOT NULL,
  qty           INT NOT NULL,
  unit_price    INT NOT NULL,
  CONSTRAINT fk_oi_order   FOREIGN KEY (order_id)   REFERENCES orders(order_id),
  CONSTRAINT fk_oi_product FOREIGN KEY (product_id) REFERENCES product(product_id),
  INDEX idx_oi_product (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 검색 로그: 맞춤 추천/통계 데이터 소스
CREATE TABLE search_log (
  log_id      INT AUTO_INCREMENT PRIMARY KEY,
  member_id   INT,
  query_text  VARCHAR(255),
  searched_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_searchlog_member FOREIGN KEY (member_id) REFERENCES member(member_id),
  INDEX idx_searchlog_member (member_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- AI(LLM) 사용량/비용 추적: 월 예산 차단의 근거 (월별 1행 누적)
CREATE TABLE ai_usage (
  yyyymm     CHAR(6) PRIMARY KEY,         -- 예: 202606
  cost_usd   DECIMAL(12,6) NOT NULL DEFAULT 0,
  calls      INT NOT NULL DEFAULT 0,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
