package com.crowd.exception;

/**
 * 保存或更新Admin是如果检测到登入账号重复抛出异常
 * @author cjn
 * @create 2020-03-14 9:56
 */
public class LoginAcctAlreadyInUseFroUpdateException extends RuntimeException {
    private static final long serialVersionUID = -6942932947973552910L;

    public LoginAcctAlreadyInUseFroUpdateException() {
    }

    public LoginAcctAlreadyInUseFroUpdateException(String message) {
        super(message);
    }

    public LoginAcctAlreadyInUseFroUpdateException(String message, Throwable cause) {
        super(message, cause);
    }

    public LoginAcctAlreadyInUseFroUpdateException(Throwable cause) {
        super(cause);
    }

    public LoginAcctAlreadyInUseFroUpdateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
