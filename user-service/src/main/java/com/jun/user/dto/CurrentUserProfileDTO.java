package com.jun.user.dto;

import lombok.Data;

@Data
public class CurrentUserProfileDTO {
    private Long id;
    private String userName;
    private String nickName;
    private String userGender;
    private String userPhone;
    private String userEmail;
    private String status;
}
