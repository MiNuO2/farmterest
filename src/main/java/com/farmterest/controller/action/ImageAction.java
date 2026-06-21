package com.farmterest.controller.action;

import java.nio.file.Files;
import java.nio.file.Path;

import com.farmterest.controller.Action;
import com.farmterest.controller.ActionForward;
import com.farmterest.util.ImageStore;
import com.farmterest.util.Params;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** 업로드된 상품 이미지를 스트리밍 서빙 (image.do?file=...). */
public class ImageAction implements Action {

    @Override
    public ActionForward execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String file = Params.str(request, "file");
        Path path = ImageStore.resolve(file);
        if (path == null || !Files.exists(path) || !Files.isRegularFile(path)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
        response.setContentType(ImageStore.contentType(file));
        response.setContentLengthLong(Files.size(path));
        response.setHeader("Cache-Control", "public, max-age=86400");
        response.setHeader("X-Content-Type-Options", "nosniff");
        Files.copy(path, response.getOutputStream());
        response.getOutputStream().flush();
        return null;  // 응답 직접 처리
    }
}
