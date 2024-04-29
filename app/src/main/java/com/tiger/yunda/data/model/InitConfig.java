package com.tiger.yunda.data.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InitConfig {
    private String from;
    private String to;
    private List<String> num;
    private List<String> name;
}
