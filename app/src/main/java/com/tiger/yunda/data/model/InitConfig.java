package com.tiger.yunda.data.model;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InitConfig implements Serializable {
    private String from;
    private String to;
    private List<String> num;
    private List<String> name;
}
