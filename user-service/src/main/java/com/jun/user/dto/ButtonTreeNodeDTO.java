package com.jun.user.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ButtonTreeNodeDTO {
    private String key;
    private String label;
    private Boolean disabled;
    private List<ButtonTreeNodeDTO> children = new ArrayList<>();
}

