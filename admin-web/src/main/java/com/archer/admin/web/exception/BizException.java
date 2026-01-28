package com.archer.admin.web.exception;

public class BizException extends RuntimeException {

    private int errorCode;
    private String errorMessage;

    public BizException(int errorCode) {
        this.errorCode = errorCode;
    }

    public BizException(int errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
