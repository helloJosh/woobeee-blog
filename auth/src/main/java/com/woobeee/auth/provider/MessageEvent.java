package com.woobeee.auth.provider;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

@Data
public class MessageEvent {
    private final JsonNode message;
}
