package com.tiger.yunda.data.model;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;
import androidx.databinding.BindingMethod;
import androidx.databinding.BindingMethods;

import com.tiger.yunda.data.BreakDownType;
import com.tiger.yunda.ui.home.TrainLocations;
import com.tiger.yunda.utils.CollectionUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 工作记录
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkLog implements Serializable {


        /**
         * 主键
         */
        private String id;
        /**
         * 任务id
         */
        private String taskId;
        /**
         * 巡检人id
         */
        private int inspectorId;
        /**
         * 巡检人
         */
        private String inspector;
        /**
         * 所在列位id
         */
        private int positionId;
        /**
         * 所在列位
         */
        private String positionName;
        /**
         * 车号
         */
        private String trainNo;
        /**
         * 巡检单元 A374CP-A
         */
        private String inspectionUnit;
        /**
         * 计划时长、耗时
         */
        private int duration;
        /**
         * 评分
         */
        private String score;

        /**
         * 评价得分
         */
        private String appealScore;
        /**
         * 定位卡号
         */
        private String locatorCardNo;
        /**
         * 状态，1:未处理、2:已处理
         */
        private int state;

        /**
         * 状态，1:下发中、2:已下发、3:巡检中、4:完成、5:已接收
         */
        private String stateText;
        /**
         * 班组/部门 DCC
         */
        private String deptName;
        /**
         * 是否故障
         */
        private int faultState;
        /**
         * 是否故障
         */
        private String faultStateText;
        /**
         * 任务下发时间
         */
        private String issuTime;
        /**
         * 任务创建时间
         */
        private String createTime;
        /**
         * 任务接受时间
         */
        private String receiveTime;
        /**
         * 任务巡检时间
         */
        private String patrolTime;
        /**
         * 任务完成时间
         */
        private String completeTime;
        /**
         * 人员轨迹url
         */
        private String staffLocationSysLink;
        /**
         * 人员轨迹参数
         */
        private SysLinkArg staffLocationSysLinkArgs;
        private Task task;
        private List<TrainLocations> trainLocations;
        private List<Faults> faults;
        private String isAppeal;



        public List<BreakDownType> getSpinnerTypes() {
                List<BreakDownType> list = new ArrayList<>();
                if (!CollectionUtil.isEmpty(trainLocations)) {
                        trainLocations.forEach(e -> {
                                BreakDownType breakDownType = new BreakDownType();
                                breakDownType.setType(e.getLocationType());
                                breakDownType.setName(e.getLocationTypeName());
                                list.add(breakDownType);
                        });
                } else {
                        return list;
                }
                return list;
        }

}
