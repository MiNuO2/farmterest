package com.farmterest.util;

/**
 * 검색/필터 조건 컨테이너.
 * 값이 채워진 항목만 ProductDAO 에서 동적 SQL(WHERE/ORDER BY)로 조립한다.
 */
public class SearchCriteria {

    /** 정렬 기준. */
    public static final String SORT_RELEVANCE = "relevance"; // 추천/기본
    public static final String SORT_PRICE_ASC = "price_asc";
    public static final String SORT_PRICE_DESC = "price_desc";
    public static final String SORT_QUALITY = "quality";     // 정백도/식미치 우선
    public static final String SORT_NEWEST = "newest";

    private String keyword;        // 자유 키워드(상품명/설명 LIKE)
    private String category;
    private String region;
    private Integer priceMin;
    private Integer priceMax;
    private Integer minPolishedRate;
    private Integer minWholeGrainRate;
    private Integer minTasteScore;
    private String sort = SORT_RELEVANCE;

    /** 자연어 해석 결과를 사람이 읽을 수 있게 요약한 문구. */
    private String understood;

    public SearchCriteria() {
    }

    public boolean hasAnyFilter() {
        return category != null || region != null || priceMin != null || priceMax != null
                || minPolishedRate != null || minWholeGrainRate != null || minTasteScore != null
                || (keyword != null && !keyword.isBlank());
    }

    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public Integer getPriceMin() { return priceMin; }
    public void setPriceMin(Integer priceMin) { this.priceMin = priceMin; }

    public Integer getPriceMax() { return priceMax; }
    public void setPriceMax(Integer priceMax) { this.priceMax = priceMax; }

    public Integer getMinPolishedRate() { return minPolishedRate; }
    public void setMinPolishedRate(Integer minPolishedRate) { this.minPolishedRate = minPolishedRate; }

    public Integer getMinWholeGrainRate() { return minWholeGrainRate; }
    public void setMinWholeGrainRate(Integer minWholeGrainRate) { this.minWholeGrainRate = minWholeGrainRate; }

    public Integer getMinTasteScore() { return minTasteScore; }
    public void setMinTasteScore(Integer minTasteScore) { this.minTasteScore = minTasteScore; }

    public String getSort() { return sort; }
    public void setSort(String sort) { this.sort = sort; }

    public String getUnderstood() { return understood; }
    public void setUnderstood(String understood) { this.understood = understood; }
}
