package com.crowd.exception;

/**
 * 用户没有登录就访问受保护的资源的异常
 * @author cjn
 * @create 2020-03-13 16:24
 */
public class AccessForbiddenException extends RuntimeException {
    public AccessForbiddenException() {
    }

    public AccessForbiddenException(String message) {
        super(message);
    }

    public AccessForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccessForbiddenException(Throwable cause) {
        super(cause);
    }

    public AccessForbiddenException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    private static final long serialVersionUID = -4494711286178620088L;
}
