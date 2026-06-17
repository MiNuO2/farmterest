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
