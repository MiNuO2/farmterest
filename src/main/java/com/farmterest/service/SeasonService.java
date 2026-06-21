package com.farmterest.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

/**
 * 제철 품목 선정.
 * 실제 출하 캘린더 대신, 지금은 '오늘 날짜'를 시드로 한 결정적 난수로 제철 품목을 고른다(테스트용).
 * - 같은 날이면 항상 같은 결과(새로고침해도 흔들리지 않음)
 * - 날짜가 바뀌면 강조 품목도 바뀜(날짜 기반 효과)
 */
public class SeasonService {

    /** 서비스가 다루는 전체 품목(표시 순서). */
    public static final List<String> CATEGORIES =
            Collections.unmodifiableList(Arrays.asList("쌀", "잡곡", "감자", "채소", "수산"));

    private static final int SEASONAL_COUNT = 2;  // 하루에 강조할 제철 품목 수

    /** 오늘의 제철 품목 목록. */
    public static List<String> seasonalCategories() {
        long seed = LocalDate.now().toEpochDay();
        List<String> shuffled = new ArrayList<>(CATEGORIES);
        Collections.shuffle(shuffled, new Random(seed));
        return new ArrayList<>(shuffled.subList(0, Math.min(SEASONAL_COUNT, shuffled.size())));
    }

    /**
     * 품목 → 제철 여부 맵(표시 순서 유지).
     * JSP에서 버튼을 그릴 때 한 번의 ${seasonalMap[cat]} 로 강조 여부를 판단한다.
     */
    public static LinkedHashMap<String, Boolean> seasonalFlags() {
        List<String> seasonal = seasonalCategories();
        LinkedHashMap<String, Boolean> map = new LinkedHashMap<>();
        for (String c : CATEGORIES) {
            map.put(c, seasonal.contains(c));
        }
        return map;
    }

    private SeasonService() {
    }
}
