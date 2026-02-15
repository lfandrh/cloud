package com.jun.auth.dto;

import lombok.Data;
import java.util.List;

@Data
public class UserInfoDTO {
    private String userId;
    private String userName;
    private List<String> roles;
    private List<String> buttons;
}
