package com.jun.user.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class MenuOperateRequest {
    private Long id;
    private Long parentId;
    private String menuType;
    private String menuName;
    private String routeName;
    private String routePath;
    private String component;
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
