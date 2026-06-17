package com.farmterest.model.dto;

import java.util.List;

import com.farmterest.util.SearchCriteria;

/** AI 검색 결과: 필터 + 자연어 설명 + 사용여부/모델/상태. */
public class AiResult {

    private SearchCriteria criteria;   // 적용할 검색 조건(LLM 또는 규칙기반)
    private String explanation;        // LLM이 쓴 한국어 추천 이유(없으면 null)
    private boolean usedLlm;           // LLM 사용 여부
    private String model;              // 사용한 모델 id
    private String status;             // ai | disabled | budget | error:...
    private List<Integer> productIds;  // LLM이 고른 추천 상품 id(정렬용)

    public SearchCriteria getCriteria() { return criteria; }
    public void setCriteria(SearchCriteria criteria) { this.criteria = criteria; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public boolean isUsedLlm() { return usedLlm; }
    public void setUsedLlm(boolean usedLlm) { this.usedLlm = usedLlm; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<Integer> getProductIds() { return productIds; }
    public void setProductIds(List<Integer> productIds) { this.productIds = productIds; }
}
