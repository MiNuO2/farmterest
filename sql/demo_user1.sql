-- 데모 계정 user1(member_id=3) 구매이력 시드.
-- 발표 시연 시 '구매이력 기반 맞춤추천'이 바로 보이도록 쌀·평창 선호 프로필을 만든다.
-- (선호 품목=쌀, 선호 지역=평창, 선호 정백도≈92 → 메인/마이페이지/검색에서 근거 있는 추천 노출)
-- 재실행 가능: user1의 기존 주문/후기를 먼저 정리한 뒤 다시 채운다.
SET NAMES utf8mb4;

DELETE r FROM review r WHERE r.member_id = 3;
DELETE oi FROM order_item oi JOIN orders o ON o.order_id = oi.order_id WHERE o.member_id = 3;
DELETE FROM orders WHERE member_id = 3;

INSERT INTO orders (member_id, total_price, status) VALUES (3, 91700, 'DONE');
SET @o = LAST_INSERT_ID();
INSERT INTO order_item (order_id, product_id, qty, unit_price) VALUES
  (@o, 4, 1, 38900),   -- 평창 고랭지 햅쌀 10kg (쌀 / 평창 / 정백도 95)
  (@o, 3, 1, 35900),   -- 강릉 해풍 추청쌀 10kg (쌀 / 강릉 / 정백도 90)
  (@o, 16, 1, 16900);  -- 평창 산나물 모듬 1kg (채소 / 평창)
