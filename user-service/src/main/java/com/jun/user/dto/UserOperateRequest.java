package com.jun.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class UserOperateRequest {
    private Long id;
    @NotBlank(message = "userName is required")
    @Size(max = 50, message = "userName length must be <= 50")
    private String userName;
    private Integer userGender;
    @Size(max = 50, message = "nickName length must be <= 50")
    private String nickName;
    @Size(max = 20, message = "userPhone length must be <= 20")
    private String userPhone;
    @Size(max = 100, message = "userEmail length must be <= 100")
    private String userEmail;
    private Integer status;
    @NotEmpty(message = "userRoles is required")
    private List<String> userRoles;
    @Size(max = 100, message = "password length must be <= 100")
    private String password;
}
