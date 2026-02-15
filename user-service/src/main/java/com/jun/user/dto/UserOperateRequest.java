package com.jun.user.dto;

import lombok.Data;
import java.util.List;

@Data
public class UserOperateRequest {
    private Long id;
    private String userName;
    private Integer userGender;
    private String nickName;
    private String userPhone;
    private String userEmail;
    private Integer status;
    private List<String> userRoles;
    private String password;
}
