package com.oshikatsu_pj.oshikatsu.oshigroup.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateOshiGroupRequest(
        @NotBlank
        Long groupId,
        @NotBlank
        String groupName,
        String company,
        @Size(max = 1000)
        String description
) {
}
