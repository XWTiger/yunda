package com.tiger.yunda.data.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResult {

    private String type;
    private String title;
    private int status;
    private String traceId;
    private Object errors;
}
