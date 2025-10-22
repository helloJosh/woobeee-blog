package com.woobeee.auth.provider;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

//@Data
public record MessageEvent (JsonNode message){
    //private final JsonNode message;
}
