package com.company.exceptions;

public class UserNotFound extends RuntimeException {

    public UserNotFound(String msg){
        super(msg);
    }

}
