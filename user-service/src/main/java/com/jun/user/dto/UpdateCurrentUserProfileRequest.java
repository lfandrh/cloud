package com.jun.user.dto;

import lombok.Data;

@Data
public class UpdateCurrentUserProfileRequest {
    private String nickName;
    private Integer userGender;
    private String userPhone;
    private String userEmail;
}
