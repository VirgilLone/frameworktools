package com.haoyunhu.tools.exception;


public class BizTokenException extends RuntimeException {

    private String errorCode;

    public String getErrorCode() {
        return errorCode;
    }

    public BizTokenException(String errorMessage){
        super(errorMessage);
    }

    public BizTokenException(String errorMessage, Throwable cause){
        super(errorMessage,cause);
    }

    public BizTokenException(String errorMessage, String errorCode){
        super(errorMessage);
        this.errorCode = errorCode;
    }

    public BizTokenException(String errorMessage, String errorCode, Throwable cause){
        super(errorMessage,cause);
        this.errorCode = errorCode;
    }
}
