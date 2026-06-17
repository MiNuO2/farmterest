package com.farmterest.util;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * OpenRouter/LLM 설정 로더.
 * 비공개 파일 ~/.farmterest/openrouter.properties 를 읽고, 환경변수 OPENROUTER_API_KEY 가 있으면 우선.
 * 파일이 수정되면 자동 재로딩(재배포 없이 키/설정 변경 반영).
 */
public class AppConfig {

    private static final Path FILE =
            Paths.get(System.getProperty("user.home"), ".farmterest", "openrouter.properties");

    private static Properties props = new Properties();
    private static long loadedMtime = -1;

    private static synchronized void refresh() {
        try {
            if (Files.exists(FILE)) {
                long m = Files.getLastModifiedTime(FILE).toMillis();
                if (m != loadedMtime) {
                    Properties p = new Properties();
                    try (InputStream in = Files.newInputStream(FILE)) {
                        p.load(new InputStreamReader(in, StandardCharsets.UTF_8));
                    }
                    props = p;
                    loadedMtime = m;
                }
            }
        } catch (Exception ignore) {
            // 설정 파일 문제는 무시 → LLM 비활성(규칙기반)로 동작
        }
    }

    public static String get(String key, String def) {
        refresh();
        String v = props.getProperty(key);
        return (v == null || v.isBlank()) ? def : v.trim();
    }

    public static String apiKey() {
        String env = System.getenv("OPENROUTER_API_KEY");
        if (env != null && !env.isBlank()) {
            return env.trim();
        }
        return get("openrouter.api.key", "");
    }

    public static String model() {
        return get("openrouter.model", "auto");
    }

    public static boolean autoModel() {
        return "auto".equalsIgnoreCase(model());
    }

    public static double monthlyBudgetUsd() {
        return parseD(get("openrouter.monthly.budget.usd", "30"), 30);
    }

    public static double budgetMarginUsd() {
        return parseD(get("openrouter.budget.margin.usd", "5"), 5);
    }

    /** 차단 기준선 = 예산 - 마진 (이 값에 도달하면 LLM 중단). */
    public static double softCapUsd() {
        return Math.max(0, monthlyBudgetUsd() - budgetMarginUsd());
    }

    /** LLM 사용 가능 여부: 켜져 있고 키가 있어야 true. */
    public static boolean llmEnabled() {
        return Boolean.parseBoolean(get("openrouter.enabled", "true")) && !apiKey().isBlank();
    }

    private static double parseD(String s, double def) {
        try { return Double.parseDouble(s); } catch (Exception e) { return def; }
    }
}
