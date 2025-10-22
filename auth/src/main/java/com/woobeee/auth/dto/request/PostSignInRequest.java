package com.woobeee.auth.dto.request;

import com.woobeee.auth.entity.enums.AuthType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

//@Data
//@NoArgsConstructor
//@AllArgsConstructor
public record PostSignInRequest (
        String loginId,
        String password,
        String nickname,
        List<AuthType> authTypes
){
//    private String loginId;
//    private String password;
//    private String nickname;
//    private List<AuthType> authTypes;
}
