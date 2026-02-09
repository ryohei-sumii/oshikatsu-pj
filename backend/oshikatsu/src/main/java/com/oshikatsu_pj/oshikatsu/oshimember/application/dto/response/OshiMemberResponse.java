package com.oshikatsu_pj.oshikatsu.oshimember.application.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record OshiMemberResponse(
        Long id,
        Long userId,
        Long groupId,
        String groupName,
        String memberName,
        String memberNameKana,
        byte gender,
        LocalDate birthDay,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
