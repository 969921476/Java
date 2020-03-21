package com.crowd.exception;

import java.io.Serializable;

/**
 * 登录失败抛出的异常
 * @author cjn
 * @create 2020-03-13 15:00
 */
public class LoginFailedException extends RuntimeException {
    private static final long serialVersionUID = 2429819156562194852L;

    public LoginFailedException() {
    }

    public LoginFailedException(String message) {
        super(message);
    }

    public LoginFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public LoginFailedException(Throwable cause) {
        super(cause);
    }

    public LoginFailedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
