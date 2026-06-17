package com.farmterest.service;

import java.util.Collections;
import java.util.List;

import com.farmterest.model.dao.SearchLogDAO;

/**
 * 실시간 인기 검색어 공급기.
 * 검색 로그 집계를 짧게(수 초) 캐시해, 페이지 새로고침마다 사실상 최신 인기어로 갱신된다.
 */
public class TrendingKeywords {

    private static volatile List<String> cache = Collections.emptyList();
    private static volatile long cachedAt = 0;
    private static final long TTL_MS = 3000;

    public static List<String> top(int limit) {
        long now = System.currentTimeMillis();
        if (now - cachedAt < TTL_MS && !cache.isEmpty()) {
            return cache;
        }
        try {
            List<String> kw = new SearchLogDAO().popularKeywords(limit);
            if (!kw.isEmpty()) {
                cache = kw;
                cachedAt = now;
            }
        } catch (Exception ignore) {
            // 실패 시 직전 캐시(또는 빈 목록) 반환 → 헤더는 정적 placeholder로 폴백
        }
        return cache;
    }
}
