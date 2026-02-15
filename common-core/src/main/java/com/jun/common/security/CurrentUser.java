package com.jun.common.security;

import lombok.Data;

import java.io.Serializable;

@Data
public class CurrentUser implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String username;
    private String nickname;
}
