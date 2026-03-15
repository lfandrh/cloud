package com.jun.user.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class UserSearchParams {
    @Min(value = 1, message = "current must be >= 1")
    private Integer current = 1;
    @Min(value = 1, message = "size must be >= 1")
    @Max(value = 200, message = "size must be <= 200")
    private Integer size = 10;
    private String userName;
    private String nickName;
    private String userPhone;
    private String userEmail;
    private Integer userGender;
    private Integer status;
}
