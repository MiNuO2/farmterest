package com.farmterest.service;

import java.util.List;

import com.farmterest.model.dto.ProductDTO;
import com.farmterest.model.dto.Recommendation;

/**
 * '품목별 최고의 상품' 선정 엔진.
 * 이달 판매량 + 별점(만족도) + 누적 판매량을 가중 합산해 한 품목의 대표 상품을 고른다.
 * 판매/후기 데이터가 아직 없으면 품질지표(식미치)로 자연스럽게 폴백한다.
 */
public class BestProductService {

    private static final double W_MONTH  = 50;   // 이달 판매 (이달의 인기 핵심 신호)
    private static final double W_RATING = 28;   // 별점 (만족도)
    private static final double W_TOTAL  = 14;   // 누적 판매 (스테디셀러)
    private static final double W_VOLUME = 0.6;  // 후기 수 신뢰 보너스(건당, 최대 10건)

    /** 후보 중 최고 상품을 근거와 함께 반환. 후보가 없으면 null. */
    public Recommendation pickBest(List<ProductDTO> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return null;
        }
        int maxMonth = 0, maxTotal = 0;
        for (ProductDTO p : candidates) {
            maxMonth = Math.max(maxMonth, p.getMonthSold());
            maxTotal = Math.max(maxTotal, p.getTotalSold());
        }

        ProductDTO best = candidates.get(0);
        double bestScore = -1;
        for (ProductDTO p : candidates) {
            double score = scoreOf(p, maxMonth, maxTotal);
            if (score > bestScore) {
                bestScore = score;
                best = p;
            }
        }
        return new Recommendation(best, Math.min(100, bestScore), reasonFor(best));
    }

    /** 한 상품의 베스트 점수(같은 품목 내 최대 판매량으로 정규화). */
    public double scoreOf(ProductDTO p, int maxMonth, int maxTotal) {
        double score = 0;
        if (maxMonth > 0) {
            score += W_MONTH * ((double) p.getMonthSold() / maxMonth);
        }
        if (maxTotal > 0) {
            score += W_TOTAL * ((double) p.getTotalSold() / maxTotal);
        }
        if (p.getReviewCount() > 0 && p.getAvgRating() != null) {
            score += W_RATING * (p.getAvgRating() / 5.0);
            score += Math.min(p.getReviewCount(), 10) * W_VOLUME;
        }
        // 판매/후기 데이터가 아직 없을 때의 변별: 식미치
        if (p.getTasteScore() != null) {
            score += p.getTasteScore() * 0.05;
        }
        return score;
    }

    private String reasonFor(ProductDTO p) {
        StringBuilder sb = new StringBuilder();
        if (p.getMonthSold() > 0) {
            sb.append("이달 ").append(p.getMonthSold()).append("개 판매");
        }
        if (p.hasRating()) {
            if (sb.length() > 0) {
                sb.append(" · ");
            }
            sb.append("별점 ").append(p.getAvgRating())
              .append(" (").append(p.getReviewCount()).append("명)");
        }
        if (sb.length() == 0) {
            if (p.getTasteScore() != null) {
                sb.append("식미치 ").append(p.getTasteScore()).append("점 우수");
            } else {
                sb.append("품목 추천 상품");
            }
        }
        return sb.toString();
    }
}
