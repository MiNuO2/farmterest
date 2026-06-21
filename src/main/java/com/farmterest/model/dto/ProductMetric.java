package com.farmterest.model.dto;

/** 상품별 지표 값 (정의 def 와 함께). */
public class ProductMetric {

    private int pmId;
    private int productId;
    private String value;
    private int sortOrder;
    private MetricDefinition def;   // 조인된 지표 정의

    public ProductMetric() {
    }

    /** 숫자로 해석 가능한 값이면 Double, 아니면 null. */
    public Double getNumericValue() {
        if (value == null) {
            return null;
        }
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** 게이지 채움 비율(0~100). 정의의 min/max 범위 안 위치. 숫자 아니면 null. */
    public Integer getGaugePercent() {
        Double v = getNumericValue();
        if (v == null || def == null || def.getGaugeMin() == null || def.getGaugeMax() == null) {
            return null;
        }
        double min = def.getGaugeMin();
        double max = def.getGaugeMax();
        if (max <= min) {
            return null;
        }
        double pct = (v - min) / (max - min) * 100.0;
        if (pct < 0) pct = 0;
        if (pct > 100) pct = 100;
        return (int) Math.round(pct);
    }

    public int getPmId() { return pmId; }
    public void setPmId(int pmId) { this.pmId = pmId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }

    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }

    public MetricDefinition getDef() { return def; }
    public void setDef(MetricDefinition def) { this.def = def; }
}
