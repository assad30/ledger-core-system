package com.ledger.system.exception;

import com.ledger.system.enums.ErrorCode;

public class BusinessException extends RuntimeException{

    private final ErrorCode errorCode;

    public BusinessException(String message,
                             ErrorCode errorCode) {

        super(message);
        this.errorCode = errorCode;
    }


    public ErrorCode getErrorCode() {
        return errorCode;
}
}
