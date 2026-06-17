package com.farmterest.service;

import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.farmterest.util.SearchCriteria;

/**
 * 한글 자연어 검색어 → SearchCriteria 변환 (규칙기반).
 * 예) "정백도 높은 평창 햅쌀 저렴하게"
 *     → category=쌀, region=평창, minPolishedRate=90, sort=가격오름차순
 */
public class QueryParser {

    // 강원 시군 (데이터에 존재하는 지역)
    private static final String[] REGIONS = {
            "철원", "강릉", "평창", "정선", "홍천", "횡성", "영월",
            "춘천", "양구", "속초", "동해", "고성", "양양", "인제", "화천", "태백", "삼척", "원주"
    };

    // 품목 동의어 사전 (대표 카테고리 → 동의어들)
    private static final Map<String, String[]> CATEGORY_SYNONYMS = new LinkedHashMap<>();
    static {
        CATEGORY_SYNONYMS.put("쌀",   new String[]{"쌀", "햅쌀", "백미", "현미", "오대", "추청", "밥쌀", "도정", "오분도"});
        CATEGORY_SYNONYMS.put("잡곡", new String[]{"잡곡", "보리", "메밀", "수수", "곡물", "혼합곡"});
        CATEGORY_SYNONYMS.put("감자", new String[]{"감자", "수미", "분홍감자"});
        CATEGORY_SYNONYMS.put("채소", new String[]{"채소", "나물", "곤드레", "버섯", "표고", "시래기", "산나물", "취나물"});
        CATEGORY_SYNONYMS.put("수산", new String[]{"수산", "황태", "오징어", "멸치", "코다리", "명태", "생선", "건어물", "해산물"});
    }

    private static final String[] STOPWORDS = {
            "정백도", "완전립", "식미치", "수분", "높은", "좋은", "최고", "품질", "상등급",
            "저렴", "싸게", "싼", "가성비", "저가", "비싼", "프리미엄", "고급", "최고급",
            "신선", "신상", "최신", "새로", "햇", "추천", "맞춤", "주세요", "찾아", "검색"
    };

    public SearchCriteria parse(String query) {
        SearchCriteria c = new SearchCriteria();
        List<String> understood = new ArrayList<>();
        String text = query == null ? "" : query.trim();

        // 1) 지역
        for (String r : REGIONS) {
            if (text.contains(r)) {
                c.setRegion(r);
                understood.add(r + " 지역");
                break;
            }
        }

        // 2) 품목
        outer:
        for (Map.Entry<String, String[]> e : CATEGORY_SYNONYMS.entrySet()) {
            for (String syn : e.getValue()) {
                if (text.contains(syn)) {
                    c.setCategory(e.getKey());
                    understood.add(e.getKey());
                    break outer;
                }
            }
        }

        // 3) 품질지표 키워드
        if (text.contains("정백도")) {
            c.setMinPolishedRate(90);
            c.setSort(SearchCriteria.SORT_QUALITY);
            understood.add("정백도 90%+");
        }
        if (text.contains("완전립")) {
            c.setMinWholeGrainRate(95);
            understood.add("완전립 95%+");
        }
        if (text.contains("식미치")) {
            c.setMinTasteScore(85);
            c.setSort(SearchCriteria.SORT_QUALITY);
            understood.add("식미치 85점+");
        }

        // 4) 가격/정렬 의도
        if (containsAny(text, "저렴", "싸게", "싼", "가성비", "저가")) {
            c.setSort(SearchCriteria.SORT_PRICE_ASC);
            understood.add("저렴한 순");
        } else if (containsAny(text, "프리미엄", "고급", "최고급", "상등급")) {
            c.setSort(SearchCriteria.SORT_QUALITY);
            understood.add("프리미엄 품질");
        } else if (containsAny(text, "비싼")) {
            c.setSort(SearchCriteria.SORT_PRICE_DESC);
            understood.add("고가 순");
        } else if (containsAny(text, "좋은", "높은", "최고", "품질")) {
            if (SearchCriteria.SORT_RELEVANCE.equals(c.getSort())) {
                c.setSort(SearchCriteria.SORT_QUALITY);
            }
            understood.add("품질 좋은 순");
        } else if (containsAny(text, "신선", "신상", "최신", "새로", "햇")) {
            c.setSort(SearchCriteria.SORT_NEWEST);
            understood.add("신상품 순");
        }

        // 5) 품목/지역이 안 잡혔으면 자유 키워드로 사용
        if (c.getCategory() == null && c.getRegion() == null) {
            String kw = stripStopwords(text);
            if (!kw.isBlank()) {
                c.setKeyword(kw);
                understood.add("'" + kw + "' 검색");
            }
        }

        c.setUnderstood(understood.isEmpty()
                ? "전체 상품"
                : String.join(" · ", understood));
        return c;
    }

    private boolean containsAny(String text, String... words) {
        for (String w : words) {
            if (text.contains(w)) {
                return true;
            }
        }
        return false;
    }

    private String stripStopwords(String text) {
        String result = text;
        for (String s : STOPWORDS) {
            result = result.replace(s, " ");
        }
        return result.replaceAll("\\s+", " ").trim();
    }
}
