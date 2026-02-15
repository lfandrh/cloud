package com.jun.user.dto;

import lombok.Data;

@Data
public class UserSearchParams {
    private Integer current = 1;
    private Integer size = 10;
    private String userName;
    private String nickName;
    private String userPhone;
    private String userEmail;
    private Integer userGender;
    private Integer status;
}
