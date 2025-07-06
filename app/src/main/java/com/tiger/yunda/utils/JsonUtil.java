package com.tiger.yunda.utils;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tiger.yunda.R;
import com.tiger.yunda.data.model.DeliverMssion;
import com.tiger.yunda.data.model.ErrorResult;
import com.tiger.yunda.ui.home.Mission;

import java.util.Objects;

public class JsonUtil {

    public static ErrorResult getObject(String errMsg, Context context) {
        Gson gson = new GsonBuilder().create();
        ErrorResult errorResult = gson.fromJson(errMsg, ErrorResult.class);
        if (Objects.nonNull(errorResult)) {
            Toast toast = Toast.makeText(context, errorResult.getTitle(), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0); //居中显示
            LinearLayout linearLayout = (LinearLayout) toast.getView();
            if (Objects.nonNull(linearLayout)) {
                TextView messageTextView = (TextView) linearLayout.getChildAt(0);
                messageTextView.setTextSize(18);//设置toast字体大小
            }
            toast.show();
        }

        return errorResult;
    }


    /**
     * 子任务转化
     * @return
     */
    public static DeliverMssion covertToDeliverMssion(Mission mission) {
        DeliverMssion deliverMssion = new DeliverMssion(mission.getInspectorId(), mission.getInspector(), mission.getPositionId(), mission.getPositionName(), mission.getInspectionUnit(), mission.getDuration());
        deliverMssion.getId().set(mission.getId());
        return deliverMssion;
    }






}
