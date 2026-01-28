package com.oshikatsu_pj.oshikatsu.oshigroup.application.dto.response;

import java.time.LocalDateTime;

public record OshiGroupResponse(
        Long id,
        Long userId,
        String groupName,
        String company,
        String description,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
