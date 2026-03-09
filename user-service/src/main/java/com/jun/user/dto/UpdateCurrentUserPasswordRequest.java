package com.jun.user.dto;

import lombok.Data;

@Data
public class UpdateCurrentUserPasswordRequest {
    private String oldPassword;
    private String newPassword;
}
