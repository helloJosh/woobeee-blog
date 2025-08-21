package com.woobeee.auth.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OauthTokenRequest {
    private String idToken;
}
