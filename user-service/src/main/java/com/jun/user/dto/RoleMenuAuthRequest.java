package com.jun.user.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class RoleMenuAuthRequest {

    @NotNull(message = "id is required")
    @Min(value = 1, message = "id must be >= 1")
    private Long id;

    private List<Long> menuIds;
}
