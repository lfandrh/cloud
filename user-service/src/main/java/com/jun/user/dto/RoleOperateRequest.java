package com.jun.user.dto;

import lombok.Data;

import java.util.List;

@Data
public class RoleOperateRequest {
    private Long id;
    private String roleName;
    private String roleCode;
    private String roleDesc;
    private String status;
    private List<Long> menuIds;
}
