package com.jun.user.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class MenuDTO {
    private Long id;
    private String createBy;
    private LocalDateTime createTime;
    private String updateBy;
    private LocalDateTime updateTime;
    private String status;

    private Long parentId;
    private String menuType;
    private String menuName;
    private String routeName;
    private String routePath;
    private String component;
    private String icon;
    private String iconType;
    private Integer order;

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
    private List<MenuDTO> children = new ArrayList<>();
}
