-- 데모용 구매이력 + 별점 (테스트로 평점/품목별 최고상품을 바로 보이게)
-- user2(member_id=4)로 이달에 여러 품목을 구매하고 별점을 남긴 상태를 만든다.
-- user1(member_id=3)은 손대지 않아 '추천 첫인상' 시연은 그대로 유지된다.
-- 재실행 가능: user2의 기존 주문/후기를 먼저 정리한 뒤 다시 채운다.
SET NAMES utf8mb4;

DELETE r FROM review r WHERE r.member_id = 4;
DELETE oi FROM order_item oi JOIN orders o ON o.order_id = oi.order_id WHERE o.member_id = 4;
DELETE FROM orders WHERE member_id = 4;

-- 주문 1 (쌀·잡곡)
INSERT INTO orders (member_id, total_price, status) VALUES (4, 116600, 'DONE');
SET @o1 = LAST_INSERT_ID();
INSERT INTO order_item (order_id, product_id, qty, unit_price) VALUES
  (@o1, 4, 2, 38900),   -- 평창 고랭지 햅쌀
  (@o1, 1, 1, 32900),   -- 철원 오대쌀 10kg
  (@o1, 11, 1, 24900);  -- 강원 혼합잡곡 5kg

-- 주문 2 (감자·채소)
INSERT INTO orders (member_id, total_price, status) VALUES (4, 67600, 'DONE');
SET @o2 = LAST_INSERT_ID();
INSERT INTO order_item (order_id, product_id, qty, unit_price) VALUES
  (@o2, 12, 3, 13900),  -- 평창 대관령 감자 (x3)
  (@o2, 15, 2, 9900),   -- 정선 곤드레 나물 (x2)
  (@o2, 6, 1, 41900);   -- 홍천 유기농 백미 10kg

-- 주문 3 (수산)
INSERT INTO orders (member_id, total_price, status) VALUES (4, 64700, 'DONE');
SET @o3 = LAST_INSERT_ID();
INSERT INTO order_item (order_id, product_id, qty, unit_price) VALUES
  (@o3, 19, 1, 28900),  -- 강릉 황태 1kg
  (@o3, 20, 2, 17900);  -- 속초 마른오징어 (x2)

-- 별점/후기: 위 구매한 주문항목(order_item)에 대해서만 작성
INSERT INTO review (product_id, member_id, order_item_id, rating, comment)
SELECT oi.product_id, 4, oi.order_item_id, v.rating, v.comment
FROM order_item oi
JOIN orders o ON o.order_id = oi.order_id
JOIN (
  SELECT 4  AS product_id, 5 AS rating, '해발 700m 햅쌀이라 그런지 밥이 정말 차지고 윤기가 좋아요. 재구매합니다.' AS comment
  UNION ALL SELECT 1,  4, '무난하고 좋은 오대쌀. 가성비 괜찮습니다.'
  UNION ALL SELECT 11, 5, '잡곡 비율이 딱 좋아서 잡곡밥 입문용으로 최고예요.'
  UNION ALL SELECT 12, 4, '포슬포슬 대관령 감자, 찌니까 정말 맛있네요.'
  UNION ALL SELECT 15, 5, '곤드레 향이 진하고 부드러워요. 곤드레밥 강추.'
  UNION ALL SELECT 6,  3, '유기농이라 믿고 먹지만 밥맛은 보통이었어요.'
  UNION ALL SELECT 19, 5, '황태 색이 곱고 국물이 시원합니다. 해장에 최고.'
  UNION ALL SELECT 20, 4, '마른오징어 두툼하고 쫄깃해요.'
) v ON v.product_id = oi.product_id
WHERE o.member_id = 4;
