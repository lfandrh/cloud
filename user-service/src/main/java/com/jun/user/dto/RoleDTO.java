package com.jun.user.dto;

import lombok.Data;

@Data
public class RoleDTO {
    private Long id;
    private String roleName;
    private String roleCode;
    private String roleDesc;
    private Integer status;
}
