package com.tiger.yunda.data.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainPlace {

    /**
     * 车号
     */
    private String no;
    private String id;

    /**
     * 车列位
     */
    private Integer positionId;
}
