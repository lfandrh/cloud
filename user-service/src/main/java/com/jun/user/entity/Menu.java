package com.jun.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("sys_menu")
public class Menu implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("parent_id")
    private Long parentId;

    @TableField("menu_type")
    private Integer menuType;

    @TableField("menu_name")
    private String menuName;

    @TableField("route_name")
    private String routeName;

    @TableField("route_path")
    private String routePath;

    @TableField("component")
    private String component;

    @TableField("icon")
    private String icon;

    @TableField("icon_type")
    private Integer iconType;

    @TableField("order_num")
    private Integer orderNum;

    @TableField("status")
    private Integer status;

    @TableField("hide_in_menu")
    private Integer hideInMenu;

    @TableField("active_menu")
    private String activeMenu;

    @TableField("create_by")
    private String createBy;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_by")
    private String updateBy;

    @TableField("update_time")
    private LocalDateTime updateTime;
}
