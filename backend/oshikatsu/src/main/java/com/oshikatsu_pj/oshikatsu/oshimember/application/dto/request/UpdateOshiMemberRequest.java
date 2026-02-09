package com.oshikatsu_pj.oshikatsu.oshimember.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.time.LocalDate;

public record UpdateOshiMemberRequest(
        @NotNull(message = "メンバーIDは必須です")
        Long memberId,

        @NotBlank(message = "メンバー名は必須です")
        String memberName,

        @NotBlank(message = "メンバー名(カナ)は必須です")
        String memberNameKana,

        @NotNull(message = "性別は必須です")
        @Min(value = 0, message = "性別は0または1を指定してください")
        @Max(value = 1, message = "性別は0または1を指定してください")
        Byte gender,

        @NotNull(message = "誕生日は必須です")
        LocalDate birthDay
) {
}
