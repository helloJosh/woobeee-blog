package com.woobeee.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostSignInRequest {
    private String loginId;
    private String password;
    private String nickname;
}
