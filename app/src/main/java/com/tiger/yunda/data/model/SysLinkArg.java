package com.tiger.yunda.data.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SysLinkArg implements Serializable {
    private String token;
    private String username;
    private InitConfig initConfig;
}
