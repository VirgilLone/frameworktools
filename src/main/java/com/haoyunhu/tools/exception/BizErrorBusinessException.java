package com.haoyunhu.tools.exception;


public class BizErrorBusinessException extends RuntimeException {

    private String errorCode;

    public String getErrorCode() {
        return errorCode;
    }

    public BizErrorBusinessException(String errorMessage){
        super(errorMessage);
    }

    public BizErrorBusinessException(String errorMessage, Throwable cause){
        super(errorMessage,cause);
    }

    public BizErrorBusinessException(String errorMessage, String errorCode){
        super(errorMessage);
        this.errorCode = errorCode;
    }

    public BizErrorBusinessException(String errorMessage, String errorCode, Throwable cause){
        super(errorMessage,cause);
        this.errorCode = errorCode;
    }
}
