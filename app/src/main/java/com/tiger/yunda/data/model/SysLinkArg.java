package com.tiger.yunda.data.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SysLinkArg {
    private String token;
    private String username;
    private InitConfig initConfig;
}
