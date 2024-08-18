package com.tiger.yunda.utils;

import com.tiger.yunda.data.model.Faults;

import java.util.List;

public class BusinessUtil {

    public static String getFaultDetailStr(List<Faults> faults) {
        if (CollectionUtil.isEmpty(faults)) {
            return "";
        } else {
            StringBuffer stringBuffer = new StringBuffer();
            faults.forEach(ele -> {
                stringBuffer.append(ele.getTrainLocationName() + " : " + ele.getDesc() + "\n");
            });
            return stringBuffer.toString();
        }
    }
}
