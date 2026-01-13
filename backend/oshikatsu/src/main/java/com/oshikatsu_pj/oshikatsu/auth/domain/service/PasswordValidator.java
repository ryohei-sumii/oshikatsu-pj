package com.oshikatsu_pj.oshikatsu.auth.domain.service;

import com.oshikatsu_pj.oshikatsu.auth.domain.config.PasswordPolicy;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * パスワードバリデーションを行うドメインサービス
 */
@Component
public class PasswordValidator {

    private final PasswordPolicy passwordPolicy;

    public PasswordValidator(PasswordPolicy passwordPolicy) {
        this.passwordPolicy = passwordPolicy;
    }

    public void validate(String password) {
        if (StringUtils.isBlank(password)) {
            throw new IllegalArgumentException("パスワードは必須です。");
        }
        String trimmedPassword = StringUtils.trim(password);

        // 最小長チェック
        if (trimmedPassword.length() < passwordPolicy.getMinLength()) {
            throw new IllegalArgumentException(
                    String.format("パスワードは%d文字以上である必要があります。", passwordPolicy.getMinLength())
            );
        }

        // 最大長チェック
        if (trimmedPassword.length() > passwordPolicy.getMaxLength()) {
            throw new IllegalArgumentException(
                    String.format("パスワードは%d文字以下である必要があります。", passwordPolicy.getMaxLength())
            );
        }

        // 大文字チェック
        if (passwordPolicy.isRequireUppercase()
                && !hasUppercase(trimmedPassword)) {
            throw new IllegalArgumentException("パスワードには大文字を含める必要があります。");
        }

        // 小文字チェック
        if (passwordPolicy.isRequireLowercase()
                && !hasLowercase(trimmedPassword)) {
            throw new IllegalArgumentException("パスワードには小文字を含める必要があります。");
        }

        // 数字チェック
        if (passwordPolicy.isRequireDigit()
                && !hasDigit(trimmedPassword)) {
            throw new IllegalArgumentException("パスワードには数字を含める必要があります。");
        }

        // 特殊文字チェック
        if (passwordPolicy.isRequireSpecialChar()
                &&  !hasSpecialChar(trimmedPassword)) {
            throw new IllegalArgumentException("パスワードには特殊文字を含める必要があります。");
        }

    }

    private boolean hasUppercase(String password) {
        return password.chars().anyMatch(Character::isUpperCase);
    }

    private boolean hasLowercase(String password) {
        return password.chars().anyMatch(Character::isLowerCase);
    }

    private boolean hasDigit(String password) {
        return password.chars().anyMatch(Character::isDigit);
    }

    private boolean hasSpecialChar(String password) {
        // 特殊文字のパターン: !@#$%^&*()_+-=[]{}|;':\",./<>?
        return password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{}|;':\"\\\\,./<>?].*");
    }
}
