package com.company.exceptions;

public class UserNotFound extends RuntimeException {
    private final String errorMessage;
    private final String errorCode;

    public UserNotFound(String msg,String code){
        super(msg);
        this.errorMessage = msg;
        this.errorCode = code;
    }
}
