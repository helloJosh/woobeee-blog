package com.woobeee.back.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetCategoryResponse {
    private Long id;
    private String name;
    private Integer count;

    private List<GetCategoryResponse> children;
}
