package com.farmterest.model.dto;

import java.util.HashSet;
import java.util.Set;

/** 구매이력에서 도출한 회원 선호 프로필 (추천·대시보드용). */
public class PreferenceProfile {

    private String topCategory;       // 가장 자주 산 품목
    private String topRegion;         // 가장 자주 산 지역
    private int avgPolishedRate;      // 구매한 쌀의 평균 정백도(0=데이터 없음)
    private int avgTasteScore;        // 평균 식미치
    private int purchaseCount;        // 구매한 상품 종류 수
    private Set<Integer> purchasedIds = new HashSet<>();

    public boolean isEmpty() {
        return purchaseCount == 0;
    }

    public String getTopCategory() { return topCategory; }
    public void setTopCategory(String topCategory) { this.topCategory = topCategory; }

    public String getTopRegion() { return topRegion; }
    public void setTopRegion(String topRegion) { this.topRegion = topRegion; }

    public int getAvgPolishedRate() { return avgPolishedRate; }
    public void setAvgPolishedRate(int avgPolishedRate) { this.avgPolishedRate = avgPolishedRate; }

    public int getAvgTasteScore() { return avgTasteScore; }
    public void setAvgTasteScore(int avgTasteScore) { this.avgTasteScore = avgTasteScore; }

    public int getPurchaseCount() { return purchaseCount; }
    public void setPurchaseCount(int purchaseCount) { this.purchaseCount = purchaseCount; }

    public Set<Integer> getPurchasedIds() { return purchasedIds; }
    public void setPurchasedIds(Set<Integer> purchasedIds) { this.purchasedIds = purchasedIds; }
}
