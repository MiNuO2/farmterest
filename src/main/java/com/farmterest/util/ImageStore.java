package com.farmterest.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.UUID;

import jakarta.servlet.http.Part;

/**
 * 업로드 상품 이미지 저장/조회.
 * 웹앱 바깥(~/.farmterest/uploads)에 저장해 재배포에도 보존되며, image.do 로 서빙한다.
 */
public class ImageStore {

    public static final Path DIR =
            Paths.get(System.getProperty("user.home"), ".farmterest", "uploads");

    /** 허용 이미지 확장자. SVG는 스크립트 삽입(저장형 XSS) 위험으로 업로드 불가. */
    private static boolean allowed(String ext) {
        switch (ext) {
            case "jpg": case "jpeg": case "png": case "gif": case "webp":
                return true;
            default:
                return false;
        }
    }

    /**
     * 업로드 파트를 저장하고 image_url 값("image.do?file=...")을 반환.
     * 파일이 없거나 이미지가 아니면 null.
     */
    public static String save(Part part) throws IOException {
        if (part == null || part.getSize() <= 0) {
            return null;
        }
        String submitted = part.getSubmittedFileName();
        if (submitted == null || submitted.isBlank()) {
            return null;
        }
        String ext = extensionOf(submitted);
        if (!allowed(ext)) {
            throw new IOException("허용되지 않는 이미지 형식: " + ext);
        }
        Files.createDirectories(DIR);
        String name = UUID.randomUUID().toString().replace("-", "") + "." + ext;
        try (InputStream in = part.getInputStream()) {
            Files.copy(in, DIR.resolve(name), StandardCopyOption.REPLACE_EXISTING);
        }
        return "image.do?file=" + name;
    }

    /** 서빙용: 파일명을 안전하게 정규화(경로 탈출 차단)한 실제 경로. */
    public static Path resolve(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return null;
        }
        String safe = Paths.get(fileName).getFileName().toString();   // 디렉터리 부분 제거
        return DIR.resolve(safe);
    }

    /** 확장자에 따른 Content-Type. */
    public static String contentType(String fileName) {
        switch (extensionOf(fileName)) {
            case "png":  return "image/png";
            case "gif":  return "image/gif";
            case "webp": return "image/webp";
            case "svg":  return "image/svg+xml";
            default:     return "image/jpeg";
        }
    }

    private static String extensionOf(String fileName) {
        int dot = fileName.lastIndexOf('.');
        if (dot < 0 || dot == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(dot + 1).toLowerCase(Locale.ROOT);
    }

    private ImageStore() {
    }
}
