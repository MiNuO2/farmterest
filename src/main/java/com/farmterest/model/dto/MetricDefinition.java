package com.farmterest.model.dto;

/** 품질지표의 '정의'(카탈로그/판매자제안/승인). 도움말은 공식 지표에만 노출. */
public class MetricDefinition {

    private int defId;
    private String metricKey;
    private String label;
    private String unit;
    private String category;
    private String status;        // CATALOG | PENDING | APPROVED | REJECTED
    private boolean goodHigh;
    private Double gaugeMin;
    private Double gaugeMax;
    private String helpSummary;
    private String helpBody;
    private int createdBy;

    public MetricDefinition() {
    }

    /** 소비자에게 공식 지표로 노출되는가(카탈로그 또는 관리자 승인). */
    public boolean isOfficial() {
        return "CATALOG".equals(status) || "APPROVED".equals(status);
    }

    /** 검토 대기(판매자 제안, 미승인). */
    public boolean isPending() {
        return "PENDING".equals(status);
    }

    /** 도움말(상세 설명)이 열람 가능한가. */
    public boolean isHasHelp() {
        return isOfficial() && helpSummary != null && !helpSummary.isBlank();
    }

    public int getDefId() { return defId; }
    public void setDefId(int defId) { this.defId = defId; }

    public String getMetricKey() { return metricKey; }
    public void setMetricKey(String metricKey) { this.metricKey = metricKey; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isGoodHigh() { return goodHigh; }
    public void setGoodHigh(boolean goodHigh) { this.goodHigh = goodHigh; }

    public Double getGaugeMin() { return gaugeMin; }
    public void setGaugeMin(Double gaugeMin) { this.gaugeMin = gaugeMin; }

    public Double getGaugeMax() { return gaugeMax; }
    public void setGaugeMax(Double gaugeMax) { this.gaugeMax = gaugeMax; }

    public String getHelpSummary() { return helpSummary; }
    public void setHelpSummary(String helpSummary) { this.helpSummary = helpSummary; }

    public String getHelpBody() { return helpBody; }
    public void setHelpBody(String helpBody) { this.helpBody = helpBody; }

    public int getCreatedBy() { return createdBy; }
    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }
}
