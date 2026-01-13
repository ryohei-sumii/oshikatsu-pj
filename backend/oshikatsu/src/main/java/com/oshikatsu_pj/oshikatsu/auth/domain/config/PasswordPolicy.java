package com.oshikatsu_pj.oshikatsu.auth.domain.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * パスワードポリシー設定
 */
@Component
@ConfigurationProperties(prefix = "password")
@Setter
@Getter
public class PasswordPolicy {
    private int minLength = 8;
    private int maxLength = 128;
    private boolean requireUppercase = true;
    private boolean requireLowercase = true;
    private boolean requireDigit = true;
    private boolean requireSpecialChar = false;
}
