-- 유연한 품질지표 시스템 (기존 DB 마이그레이션)
-- metric_definition: 지표의 '정의'(카탈로그/판매자제안/승인). 도움말은 승인된 지표에만.
-- product_metric   : 상품별 지표 '값'.
-- 실행: mysql -u root -p farmterest < sql\metrics.sql
SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS metric_definition (
  def_id       INT AUTO_INCREMENT PRIMARY KEY,
  metric_key   VARCHAR(60) NOT NULL UNIQUE,                 -- 슬러그(카탈로그) 또는 정규화 라벨(커스텀)
  label        VARCHAR(60) NOT NULL,
  unit         VARCHAR(20),
  category     VARCHAR(30),                                 -- 제안 카테고리(null=공통/커스텀)
  status       ENUM('CATALOG','PENDING','APPROVED','REJECTED') NOT NULL DEFAULT 'PENDING',
  good_high    TINYINT NOT NULL DEFAULT 1,                  -- 1=높을수록 좋음
  gauge_min    DECIMAL(10,3),
  gauge_max    DECIMAL(10,3),
  help_summary VARCHAR(400),
  help_body    TEXT,
  created_by   INT,
  created_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_md_status (status),
  INDEX idx_md_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS product_metric (
  pm_id      INT AUTO_INCREMENT PRIMARY KEY,
  product_id INT NOT NULL,
  def_id     INT NOT NULL,
  value      VARCHAR(40) NOT NULL,
  sort_order INT NOT NULL DEFAULT 0,
  CONSTRAINT fk_pm_product FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE CASCADE,
  CONSTRAINT fk_pm_def     FOREIGN KEY (def_id)     REFERENCES metric_definition(def_id),
  INDEX idx_pm_product (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 재실행 시 카탈로그/데모값 초기화(판매자가 만든 PENDING/APPROVED 커스텀은 보존)
DELETE FROM product_metric;
DELETE FROM metric_definition WHERE status = 'CATALOG';

-- ===== 카탈로그 지표(조사·검증본). status=CATALOG → 소비자 도움말 즉시 열람 =====
INSERT INTO metric_definition (metric_key, label, unit, category, status, good_high, gauge_min, gauge_max, help_summary, help_body) VALUES
-- 잡곡
('grain_protein','단백질 함량','%','잡곡','CATALOG',1,6,16,
 '잡곡 100g 중 단백질 비율로, 곡물의 영양 밀도를 숫자로 비교하는 가장 기본 지표.',
 '<p>잡곡 100g에 든 단백질의 무게 비율(%)입니다. 보리·메밀·수수는 백미(약 6~7%)보다 단백질이 많은 경우가 많아 "영양 가성비"를 비교하기 좋습니다.</p><p>국가표준식품성분표 기준 도정 보리는 약 9%대, 메밀은 12~14%로 곡류 중 높은 편입니다. 품종·도정에 따라 8~15%까지 차이가 납니다.</p><p class="qh-tip"><b>구매 팁</b> 같은 단위(100g당 g/%)로 표기된 제품끼리 비교하세요. 메밀은 12% 이상이면 양호합니다.</p>'),
('beta_glucan','베타글루칸(보리)','%','잡곡','CATALOG',1,2,15,
 '보리에 든 수용성 식이섬유 베타글루칸 비율로, 보리의 건강 기능성을 숫자로 비교하는 지표.',
 '<p>베타글루칸은 보리·귀리에 풍부한 수용성 식이섬유로 혈중 콜레스테롤·혈당 관리와 관련해 주목받습니다. 같은 보리라도 품종에 따라 함량이 크게 달라 비교에 유용합니다.</p><p>일반 보리는 약 4~6%, 고함량 품종 "베타원"은 약 11~12%, 2024년 신품종 "베타헬스"는 약 14.2%까지 보고됩니다. 보리 상품끼리 비교하는 지표입니다(메밀·수수와 직접 비교 X).</p><p class="qh-tip"><b>구매 팁</b> 5% 안팎은 평범, 10% 이상이면 고함량입니다.</p>'),
('rutin','루틴(메밀)','mg/100g','잡곡','CATALOG',1,0,3000,
 '메밀의 대표 기능성분 루틴의 100g당 함량으로, 단메밀과 쓴메밀을 확실히 구분하는 지표.',
 '<p>루틴은 메밀의 플라보노이드 기능성분(항산화·모세혈관)으로 100g당 mg으로 표시합니다. 메밀 종류에 따라 함량 차이가 매우 큽니다.</p><p>보통(단)메밀은 약 23mg/100g인 반면 쓴메밀 종자는 약 2,700mg까지로 수십~100배 이상 높습니다. 볶음·가공 시 줄 수 있어 원료곡 기준인지 확인하세요.</p><p class="qh-tip"><b>구매 팁</b> 기능성 위주면 "쓴메밀(타타리메밀)" 표기 + 루틴 수치를 확인하세요.</p>'),
-- 감자
('specific_gravity','비중','','감자','CATALOG',1,1.05,1.1,
 '감자 속 고형분(전분) 함량을 물 대비로 나타낸 값으로, 포슬포슬함(분질성)을 가늠하는 핵심 지표.',
 '<p>같은 부피의 물 대비 감자 무게입니다. 높을수록 전분이 많아 포슬포슬(분질), 낮을수록 수분이 많아 촉촉(점질)합니다.</p><p>약 1.08 이상이면 분질로 굽기·찜·튀김에 좋고, 1.07 미만이면 점질로 조림·샐러드에 적합합니다. 1.070 미만은 가공용 규격 미달로 봅니다.</p><p class="qh-tip"><b>구매 팁</b> 6~10% 소금물에 담가 가라앉으면 분질(포슬), 뜨면 점질(촉촉)로 간단히 구분됩니다.</p>'),
('dry_matter','건물률(고형분)','%','감자','CATALOG',1,15,25,
 '감자에서 수분을 뺀 고형분 비율로, 대부분이 전분이라 비중과 함께 가공적성을 결정.',
 '<p>전체 무게에서 수분을 뺀 고형분 비율(%)이며 대부분이 전분입니다. 건물률↑ → 전분↑ → 비중↑로 서로 연동됩니다. 생감자(수미)는 약 18~19% 수준입니다.</p><p>감자칩용은 약 20~23%, 프렌치프라이용은 21~23%가 우수 기준입니다. 높으면 튀길 때 기름 흡수가 적고 바삭합니다.</p><p class="qh-tip"><b>구매 팁</b> 직접 보긴 어려우니 비중(소금물 테스트)·분질 품종 표기로 가늠하세요.</p>'),
('potato_size','크기·규격(개당 무게)','g','감자','CATALOG',1,40,300,
 '농산물 표준규격(NAQS)상 감자 1개당 무게로 정한 크기 구분(3L~S).',
 '<p>국립농산물품질관리원 표준규격은 개당 무게로 크기를 나눕니다: 3L 280g↑, 2L 220~280, L 160~220, M 100~160, S 40~100g. 품위 등급은 특·상·보통입니다.</p><p>크기가 크고 고른(고르기 우수) 감자가 상위 등급으로 거래됩니다. 통구이는 큰 것(2L↑), 조림·찜은 중간(M)이 적합합니다.</p><p class="qh-tip"><b>구매 팁</b> 포장의 크기 호칭(2L·L·M)과 등급(특·상)을 함께 확인하세요.</p>'),
-- 채소
('veg_moisture','수분·신선도','%','채소','CATALOG',1,80,96,
 '채소 100g 중 물의 비율로, 신선도를 가장 직접적으로 보여주는 지표.',
 '<p>채소 무게 중 수분 비율(%)입니다. 잎채소·나물은 대체로 88~93%로 높고, 수확 후 시들수록 빠르게 줄어 같은 품목이면 수분이 높게 유지된 것이 신선합니다.</p><p>표준규격도 "신선도"를 공식 품질구분 항목으로 씁니다. (시래기·말린나물 등 건채소는 반대로 수분이 낮을수록 보관성↑이라 생채소와 같은 기준으로 비교 X)</p><p class="qh-tip"><b>구매 팁</b> 잎이 빳빳하고 묵직하며 절단면이 촉촉한 것을 고르세요.</p>'),
('brix','당도','°Bx','채소','CATALOG',1,3,12,
 '굴절당도계로 재는 즙의 당 농도(°Brix)로, 같은 품목끼리 단맛을 비교하는 참고 지표.',
 '<p>즙에 녹은 당 등 가용성 고형물 농도입니다(1°Bx≈100g 중 1g). 식약처 식품공전에도 굴절계 당도측정법이 공식 시험법으로 있습니다.</p><p>채소는 과일과 달리 공식 등급 항목이 아니고 품목별 편차가 큽니다. 절대값보다 "같은 품목 안에서 어느 쪽이 높은가"로 비교하세요.</p><p class="qh-tip"><b>구매 팁</b> 표기가 없으면 제철·당일수확·색이 진하고 단단한 것이 대체로 풍미가 좋습니다.</p>'),
('mushroom_grade','갓 두께·펴짐 등급(버섯)','등급','채소','CATALOG',1,1,3,
 '표고 등 버섯의 품질을 가르는 지표로, 갓이 두껍고 덜 펴졌을수록 상품.',
 '<p>표고버섯은 갓의 펴짐 정도와 육질(두께)로 품질을 나눕니다. 갓이 거의 안 펴지고 거북등처럼 갈라진 두꺼운 것을 "화고"(최상), 거의 안 펴진 것을 "동고"(상), 더 펴진 것을 "향고·향신"(보통)으로 봅니다.</p><p>갓이 두껍고 덜 펴질수록 조직이 치밀해 식감이 쫄깃하고 향이 진합니다. (버섯은 임산물로, 일반 나물엔 적용 X)</p><p class="qh-tip"><b>구매 팁</b> 갓이 봉오리지듯 덜 펴지고 두꺼우며 뒷면 주름이 밝은 것을 고르세요. 등급 3=화고급, 2=동고, 1=향고/향신.</p>'),
-- 수산 (건어물: 수분은 낮을수록 유리한 경우가 많아 good_high=0)
('fish_moisture','수분 함량','%','수산','CATALOG',0,0,40,
 '건어물 100g 중 물의 비율로, 건조도와 저장성을 가늠하는 기본 지표(반건조 제외).',
 '<p>건해산물의 수분 비율(%)입니다. 같은 무게면 수분이 많을수록 "물값"을 더 내는 셈이고, 높을수록 곰팡이·산패가 빨라 저장성이 떨어집니다.</p><p>완전건조(마른멸치·마른오징어)는 수분이 낮고, 황태는 낮은 편, 코다리·피데기는 반건조라 수분이 높은 게 정상입니다. 완전건조와 반건조를 직접 비교하면 안 됩니다.</p><p class="qh-tip"><b>구매 팁</b> 같은 품목·같은 가격이면 수분이 낮은(실살이 많은) 것이 유리. 완전건조는 바삭하게 부러지는 것을 고르세요.</p>'),
('fish_protein','단백질 함량','%','수산','CATALOG',1,0,90,
 '건어물 100g 중 단백질 무게(%)로, 건해산물의 영양 가치를 숫자로 비교하는 핵심 지표.',
 '<p>건제품은 수분이 빠진 만큼 단백질이 농축됩니다. 황태(건조)는 100g당 약 80g, 마른오징어는 약 59~68g으로 일반 식품 중 최상위권입니다.</p><p>높을수록 영양 밀도가 높지만 마른멸치·황태는 나트륨도 높은 편이라 함께 봐야 합니다.</p><p class="qh-tip"><b>구매 팁</b> 영양성분표의 "100g당 단백질(g)"을 비교하고, 나트륨도 함께 확인하세요.</p>'),
('fish_size','크기·마리수 등급','mm','수산','CATALOG',1,0,100,
 '건해산물을 길이(mm)나 마리수(축당·마리당 g)로 나눈 표준 크기 등급으로, 용도와 가격을 결정.',
 '<p>건해산물의 친숙한 숫자 기준입니다. 마른멸치는 전장으로 세멸(1.5cm↓)·자멸(1.6~3.0)·소멸(3.1~4.5)·중멸(4.6~7.6)·대멸(7.7cm↑)로 나뉘고 용도가 다릅니다.</p><p>마른오징어는 1축(20마리)·마리당 무게로 크기를 가르며, 마리당 무게가 클수록 두툼해 상품성이 높습니다.</p><p class="qh-tip"><b>구매 팁</b> 멸치는 용도에 맞는 길이(볶음=세멸~소멸, 육수=대멸)를, 오징어는 마리당 g을 확인하세요.</p>');

-- ===== 데모 상품에 지표값 시드(비교가 바로 보이도록) =====
-- 감자
INSERT INTO product_metric (product_id, def_id, value, sort_order)
SELECT 12, def_id, v, so FROM (SELECT 'specific_gravity' k,'1.085' v,0 so UNION ALL SELECT 'dry_matter','20',1 UNION ALL SELECT 'potato_size','220',2) t JOIN metric_definition d ON d.metric_key=t.k;
INSERT INTO product_metric (product_id, def_id, value, sort_order)
SELECT 13, def_id, v, so FROM (SELECT 'specific_gravity' k,'1.072' v,0 so UNION ALL SELECT 'dry_matter','18',1 UNION ALL SELECT 'potato_size','150',2) t JOIN metric_definition d ON d.metric_key=t.k;
INSERT INTO product_metric (product_id, def_id, value, sort_order)
SELECT 14, def_id, v, so FROM (SELECT 'specific_gravity' k,'1.068' v,0 so UNION ALL SELECT 'dry_matter','17',1 UNION ALL SELECT 'potato_size','190',2) t JOIN metric_definition d ON d.metric_key=t.k;
-- 잡곡
INSERT INTO product_metric (product_id, def_id, value, sort_order)
SELECT 8, def_id, v, so FROM (SELECT 'grain_protein' k,'9.5' v,0 so UNION ALL SELECT 'beta_glucan','5.5',1) t JOIN metric_definition d ON d.metric_key=t.k;
INSERT INTO product_metric (product_id, def_id, value, sort_order)
SELECT 9, def_id, v, so FROM (SELECT 'grain_protein' k,'13.0' v,0 so UNION ALL SELECT 'rutin','24',1) t JOIN metric_definition d ON d.metric_key=t.k;
INSERT INTO product_metric (product_id, def_id, value, sort_order)
SELECT 11, def_id, v, so FROM (SELECT 'grain_protein' k,'10.0' v,0 so UNION ALL SELECT 'beta_glucan','5.0',1) t JOIN metric_definition d ON d.metric_key=t.k;
-- 채소
INSERT INTO product_metric (product_id, def_id, value, sort_order)
SELECT 15, def_id, v, so FROM (SELECT 'veg_moisture' k,'91' v,0 so) t JOIN metric_definition d ON d.metric_key=t.k;
INSERT INTO product_metric (product_id, def_id, value, sort_order)
SELECT 16, def_id, v, so FROM (SELECT 'veg_moisture' k,'89' v,0 so UNION ALL SELECT 'brix','5.0',1) t JOIN metric_definition d ON d.metric_key=t.k;
INSERT INTO product_metric (product_id, def_id, value, sort_order)
SELECT 17, def_id, v, so FROM (SELECT 'mushroom_grade' k,'3' v,0 so) t JOIN metric_definition d ON d.metric_key=t.k;
-- 수산
INSERT INTO product_metric (product_id, def_id, value, sort_order)
SELECT 19, def_id, v, so FROM (SELECT 'fish_moisture' k,'18' v,0 so UNION ALL SELECT 'fish_protein','80',1) t JOIN metric_definition d ON d.metric_key=t.k;
INSERT INTO product_metric (product_id, def_id, value, sort_order)
SELECT 20, def_id, v, so FROM (SELECT 'fish_moisture' k,'22' v,0 so UNION ALL SELECT 'fish_protein','62',1 UNION ALL SELECT 'fish_size','80',2) t JOIN metric_definition d ON d.metric_key=t.k;
INSERT INTO product_metric (product_id, def_id, value, sort_order)
SELECT 21, def_id, v, so FROM (SELECT 'fish_moisture' k,'15' v,0 so UNION ALL SELECT 'fish_protein','50',1 UNION ALL SELECT 'fish_size','35',2) t JOIN metric_definition d ON d.metric_key=t.k;
INSERT INTO product_metric (product_id, def_id, value, sort_order)
SELECT 22, def_id, v, so FROM (SELECT 'fish_moisture' k,'35' v,0 so UNION ALL SELECT 'fish_protein','40',1) t JOIN metric_definition d ON d.metric_key=t.k;
