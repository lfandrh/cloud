package com.jun.user.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateCurrentUserProfileRequest {
    @Size(max = 50, message = "nickName length must be <= 50")
    private String nickName;
    private Integer userGender;
    @Size(max = 20, message = "userPhone length must be <= 20")
    private String userPhone;
    @Size(max = 100, message = "userEmail length must be <= 100")
    private String userEmail;
}
