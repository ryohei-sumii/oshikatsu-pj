package com.oshikatsu_pj.oshikatsu.auth.application.service;

import com.oshikatsu_pj.oshikatsu.auth.domain.service.PasswordValidator;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class AuthService {

    private final PasswordValidator passwordValidator;

    public AuthService(PasswordValidator passwordValidator) {
        this.passwordValidator = passwordValidator;
    }
}
