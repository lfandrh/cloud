package com.jun.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class MenuOperateRequest {
    private Long id;
    private Long parentId;
    @NotBlank(message = "menuType is required")
    private String menuType;
    @NotBlank(message = "menuName is required")
    @Size(max = 50, message = "menuName length must be <= 50")
    private String menuName;
    @Size(max = 50, message = "routeName length must be <= 50")
    private String routeName;
    @Size(max = 255, message = "routePath length must be <= 255")
    private String routePath;
    @Size(max = 255, message = "component length must be <= 255")
    private String component;
    @Size(max = 50, message = "icon length must be <= 50")
    private String icon;
    private String iconType;
    private Integer order;
    private String status;

    private String i18nKey;
    private Boolean keepAlive;
    private Boolean constant;
    private String href;
    private Boolean hideInMenu;
    private String activeMenu;
    private Boolean multiTab;
    private Integer fixedIndexInTab;
    private List<Map<String, String>> query = new ArrayList<>();
    private List<MenuButtonDTO> buttons = new ArrayList<>();
}
