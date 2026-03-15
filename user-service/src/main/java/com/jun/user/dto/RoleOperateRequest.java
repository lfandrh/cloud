package com.jun.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class RoleOperateRequest {
    private Long id;
    @NotBlank(message = "roleName is required")
    @Size(max = 50, message = "roleName length must be <= 50")
    private String roleName;
    @NotBlank(message = "roleCode is required")
    @Size(max = 50, message = "roleCode length must be <= 50")
    private String roleCode;
    @Size(max = 255, message = "roleDesc length must be <= 255")
    private String roleDesc;
    private String status;
    private List<Long> menuIds;
    private List<Long> buttonIds;
}
