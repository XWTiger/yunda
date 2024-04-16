package com.tiger.yunda.data.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Train {
    //id
    private Integer value;

    //列车编号
    private String text;
}
