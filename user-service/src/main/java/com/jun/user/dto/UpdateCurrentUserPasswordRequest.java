package com.jun.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateCurrentUserPasswordRequest {
    @NotBlank(message = "oldPassword is required")
    @Size(min = 6, max = 100, message = "oldPassword length must be between 6 and 100")
    private String oldPassword;
    @NotBlank(message = "newPassword is required")
    @Size(min = 6, max = 100, message = "newPassword length must be between 6 and 100")
    private String newPassword;
}
