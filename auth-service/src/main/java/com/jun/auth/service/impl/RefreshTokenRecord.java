package com.jun.auth.service.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
class RefreshTokenRecord {
    private Long userId;
    private String tokenId;
    private Long expireAtEpochSecond;
    private Integer status;

    boolean isActive() {
        return status != null && status == 1;
    }
}

