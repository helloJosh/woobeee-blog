package com.woobeee.back.service;

import com.woobeee.back.dto.IdempotencyResult;

public interface IdempotencyService {
    IdempotencyResult begin(String clientId, String idemKey, String requestHash);
    void complete(String clientId, String idemKey, int code, Object body);
    void fail(String clientId, String idemKey, int code, Object body);
}
