package com.farmterest.controller;

/**
 * Action 실행 후 이동할 뷰 정보.
 * redirect=false 면 forward(요청 유지), true 면 sendRedirect(새 요청).
 */
public class ActionForward {

    private String path;
    private boolean redirect;

    public ActionForward() {
    }

    public ActionForward(String path, boolean redirect) {
        this.path = path;
        this.redirect = redirect;
    }

    /** forward 용 간편 생성. */
    public static ActionForward forward(String path) {
        return new ActionForward(path, false);
    }

    /** redirect 용 간편 생성. */
    public static ActionForward redirect(String path) {
        return new ActionForward(path, true);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isRedirect() {
        return redirect;
    }

    public void setRedirect(boolean redirect) {
        this.redirect = redirect;
    }
}
