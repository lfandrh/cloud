package com.jun.user.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MenuTreeDTO {
    private Long id;
    private String label;
    private Long pId;
    private List<MenuTreeDTO> children = new ArrayList<>();
}
