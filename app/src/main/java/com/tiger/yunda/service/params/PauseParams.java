package com.tiger.yunda.service.params;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PauseParams implements Serializable {

    /**
     * 车巡检位置id
     */
    private String id;

    /**
     * 动作 1:暂停、2：继续
     */
    private int action;
}
