package com.jun.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("sys_menu_button")
public class MenuButton implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("menu_id")
    private Long menuId;

    @TableField("button_name")
    private String buttonName;

    @TableField("button_code")
    private String buttonCode;

    @TableField("status")
    private Integer status;
}
