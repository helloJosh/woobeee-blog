package com.woobeee.auth.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//@Data
//@AllArgsConstructor
//@NoArgsConstructor
public record PostLoginRequest (
        String loginId,
        String password
){
//    private String loginId;
//    private String password;
}
