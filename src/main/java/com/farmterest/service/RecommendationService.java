package com.farmterest.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.farmterest.model.dao.OrderDAO;
import com.farmterest.model.dto.PreferenceProfile;
import com.farmterest.model.dto.ProductDTO;
import com.farmterest.model.dto.Recommendation;

/**
 * 규칙기반 맞춤 추천 엔진.
 * 구매이력에서 선호 프로필을 만들고, 후보 상품을 점수화해 '근거'와 함께 추천한다.
 */
public class RecommendationService {

    private final OrderDAO orderDAO = new OrderDAO();

    // 점수 가중치
    private static final double W_CATEGORY = 40;
    private static final double W_REGION = 20;
    private static final double W_POLISHED = 25;
    private static final double W_TASTE = 15;

    /** 구매이력 → 선호 프로필. */
    public PreferenceProfile buildProfile(int memberId) throws Exception {
        List<ProductDTO> bought = orderDAO.findPurchasedProducts(memberId);
        PreferenceProfile profile = new PreferenceProfile();
        profile.setPurchaseCount(bought.size());
        if (bought.isEmpty()) {
            return profile;
        }

        Map<String, Integer> catCount = new HashMap<>();
        Map<String, Integer> regionCount = new HashMap<>();
        int polishedSum = 0, polishedN = 0;
        int tasteSum = 0, tasteN = 0;

        for (ProductDTO p : bought) {
            profile.getPurchasedIds().add(p.getProductId());
            catCount.merge(p.getCategory(), 1, Integer::sum);
            regionCount.merge(p.getRegion(), 1, Integer::sum);
            if (p.getPolishedRate() != null) {
                polishedSum += p.getPolishedRate();
                polishedN++;
            }
            if (p.getTasteScore() != null) {
                tasteSum += p.getTasteScore();
                tasteN++;
            }
        }

        profile.setTopCategory(topKey(catCount));
        profile.setTopRegion(topKey(regionCount));
        profile.setAvgPolishedRate(polishedN > 0 ? polishedSum / polishedN : 0);
        profile.setAvgTasteScore(tasteN > 0 ? tasteSum / tasteN : 0);
        return profile;
    }

    /**
     * 후보 상품을 선호 프로필로 점수화해 상위 추천 반환(이미 산 상품 제외).
     * 구매이력이 없으면 빈 리스트(호출부에서 인기상품으로 대체).
     */
    public List<Recommendation> recommend(int memberId, List<ProductDTO> candidates, int limit) throws Exception {
        PreferenceProfile profile = buildProfile(memberId);
        if (profile.isEmpty()) {
            return new ArrayList<>();
        }
        return rank(profile, candidates, limit);
    }

    /** 이미 만들어진 프로필로 점수화 (프로필 재사용 시). */
    public List<Recommendation> rank(PreferenceProfile profile, List<ProductDTO> candidates, int limit) {
        List<Recommendation> recs = new ArrayList<>();
        for (ProductDTO p : candidates) {
            if (profile.getPurchasedIds().contains(p.getProductId())) {
                continue; // 이미 산 상품은 추천 제외
            }
            double score = 0;
            List<String> reasons = new ArrayList<>();

            if (p.getCategory().equals(profile.getTopCategory())) {
                score += W_CATEGORY;
                reasons.add("자주 구매한 '" + profile.getTopCategory() + "'");
            }
            if (p.getRegion().equals(profile.getTopRegion())) {
                score += W_REGION;
                reasons.add(profile.getTopRegion() + " 지역");
            }
            if (profile.getAvgPolishedRate() > 0 && p.getPolishedRate() != null
                    && p.getPolishedRate() >= profile.getAvgPolishedRate() - 3) {
                score += W_POLISHED;
                reasons.add("선호 정백도 " + profile.getAvgPolishedRate() + "%대");
            }
            if (profile.getAvgTasteScore() > 0 && p.getTasteScore() != null
                    && p.getTasteScore() >= profile.getAvgTasteScore() - 3) {
                score += W_TASTE;
                reasons.add("식미치 우수");
            }
            // 재고 보너스(품절 임박 제외)
            score += Math.min(p.getStock(), 10) * 0.3;

            if (!reasons.isEmpty()) {
                String reason = "회원님 취향: " + String.join(" · ", reasons);
                recs.add(new Recommendation(p, Math.min(score, 100), reason));
            }
        }
        recs.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));
        return recs.size() > limit ? recs.subList(0, limit) : recs;
    }

    /**
     * 검색 결과 '재정렬'용: 후보 전체를 취향 점수로 정렬하되 어느 것도 누락하지 않는다.
     * 취향에 맞는 상품엔 근거를 붙이고, 이미 산 상품은 '이전에 구매'로 뒤로 보낸다.
     * (rank()는 추천 subset만 남기는 반면, 이건 검색 결과를 보존한 채 순서만 바꾼다.)
     */
    public List<Recommendation> rankAll(PreferenceProfile profile, List<ProductDTO> candidates) {
        List<Recommendation> recs = new ArrayList<>();
        for (ProductDTO p : candidates) {
            double score = 0;
            List<String> reasons = new ArrayList<>();
            boolean owned = profile.getPurchasedIds().contains(p.getProductId());

            if (p.getCategory().equals(profile.getTopCategory())) {
                score += W_CATEGORY;
                reasons.add("자주 구매한 '" + profile.getTopCategory() + "'");
            }
            if (p.getRegion().equals(profile.getTopRegion())) {
                score += W_REGION;
                reasons.add(profile.getTopRegion() + " 지역");
            }
            if (profile.getAvgPolishedRate() > 0 && p.getPolishedRate() != null
                    && p.getPolishedRate() >= profile.getAvgPolishedRate() - 3) {
                score += W_POLISHED;
                reasons.add("선호 정백도 " + profile.getAvgPolishedRate() + "%대");
            }
            if (profile.getAvgTasteScore() > 0 && p.getTasteScore() != null
                    && p.getTasteScore() >= profile.getAvgTasteScore() - 3) {
                score += W_TASTE;
                reasons.add("식미치 우수");
            }
            score += Math.min(p.getStock(), 10) * 0.3;

            String reason;
            if (owned) {
                reason = "이전에 구매한 상품";
                score -= 50;                 // 이미 산 건 뒤로
            } else if (!reasons.isEmpty()) {
                reason = "회원님 취향: " + String.join(" · ", reasons);
            } else {
                reason = null;               // 근거 없으면 칩 없이 그대로 노출
            }
            recs.add(new Recommendation(p, Math.max(0, Math.min(score, 100)), reason));
        }
        recs.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));
        return recs;
    }

    /**
     * 콜드스타트(구매이력 없음) 추천: 별점·품질 신호로 '근거 있는' 추천을 만든다.
     * 새 소비자에게도 맞춤추천 영역이 비지 않도록 한다.
     */
    public List<Recommendation> coldStart(List<ProductDTO> candidates, int limit) {
        List<Recommendation> recs = new ArrayList<>();
        for (ProductDTO p : candidates) {
            double score = 0;
            List<String> reasons = new ArrayList<>();
            if (p.getReviewCount() > 0 && p.getAvgRating() != null) {
                score += p.getAvgRating() * 12 + Math.min(p.getReviewCount(), 10);
                reasons.add("별점 " + p.getAvgRating() + " (" + p.getReviewCount() + "명)");
            }
            if (p.getTasteScore() != null && p.getTasteScore() >= 85) {
                score += p.getTasteScore() * 0.4;
                reasons.add("식미치 " + p.getTasteScore() + "점");
            }
            if (p.getPolishedRate() != null && p.getPolishedRate() >= 92) {
                score += 8;
                reasons.add("정백도 " + p.getPolishedRate() + "%");
            }
            score += Math.min(p.getStock(), 10) * 0.2;
            if (reasons.isEmpty()) {
                continue;                    // 내세울 근거가 없으면 콜드스타트 추천에서 제외
            }
            recs.add(new Recommendation(p, Math.min(score, 100), "지금 믿고 살 만한 · " + String.join(" · ", reasons)));
        }
        recs.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));
        return recs.size() > limit ? recs.subList(0, limit) : recs;
    }

    private String topKey(Map<String, Integer> map) {
        String best = null;
        int max = -1;
        for (Map.Entry<String, Integer> e : map.entrySet()) {
            if (e.getValue() > max) {
                max = e.getValue();
                best = e.getKey();
            }
        }
        return best;
    }
}
