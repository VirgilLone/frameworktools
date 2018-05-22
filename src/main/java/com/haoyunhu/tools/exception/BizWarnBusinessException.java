package com.haoyunhu.tools.exception;


public class BizWarnBusinessException extends RuntimeException {

    private String errorCode;

    public String getErrorCode() {
        return errorCode;
    }

    public BizWarnBusinessException(String errorMessage){
        super(errorMessage);
    }

    public BizWarnBusinessException(String errorMessage, Throwable cause){
        super(errorMessage,cause);
    }

    public BizWarnBusinessException(String errorMessage, String errorCode){
        super(errorMessage);
        this.errorCode = errorCode;
    }

    public BizWarnBusinessException(String errorMessage, String errorCode, Throwable cause){
        super(errorMessage,cause);
        this.errorCode = errorCode;
    }
}
