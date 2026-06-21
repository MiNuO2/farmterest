-- 기존 farmterest DB에 review 테이블만 추가 (데이터 보존 마이그레이션)
-- 실행: mysql -u root -p farmterest < sql\review.sql
SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS review (
  review_id     INT AUTO_INCREMENT PRIMARY KEY,
  product_id    INT NOT NULL,
  member_id     INT NOT NULL,
  order_item_id INT,
  rating        TINYINT NOT NULL,
  comment       VARCHAR(500),
  created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_review_product FOREIGN KEY (product_id) REFERENCES product(product_id),
  CONSTRAINT fk_review_member  FOREIGN KEY (member_id)  REFERENCES member(member_id),
  CONSTRAINT chk_review_rating CHECK (rating BETWEEN 1 AND 5),
  UNIQUE KEY uq_review_member_item (member_id, order_item_id),
  INDEX idx_review_product (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
