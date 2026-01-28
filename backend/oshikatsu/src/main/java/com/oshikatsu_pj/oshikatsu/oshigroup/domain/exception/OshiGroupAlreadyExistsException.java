package com.oshikatsu_pj.oshikatsu.oshigroup.domain.exception;

public class OshiGroupAlreadyExistsException extends RuntimeException {
    public OshiGroupAlreadyExistsException(String message) {
        super(message);
    }
}
