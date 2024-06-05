package com.tiger.yunda.utils;




import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;

public class TimeUtil {


    public static String getDateStringFromMs(Long ms) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        date.setTime(ms);
       return sdf.format(date);
    }

    /**
     * 2024年x月x日
     * @param date
     * @return
     */
    public static String getDateYmdFromMs(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = "";
        if (Objects.isNull(date)) {
            Date now = new Date();
            formattedDate = sdf.format(now);
        }
        formattedDate = sdf.format(date);
        String[] dateParts = formattedDate.split("-");
        String year = dateParts[0];
        String month = dateParts[1];
        String day = dateParts[2];
        return year + "年" + month + "月" + day + "日";
    }

    public static Date getStartOfDay(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = "";
        if (Objects.isNull(date)) {
            Date now = new Date();
            formattedDate = sdf.format(now);
        }
        formattedDate = sdf.format(date);
        String dateStr = formattedDate + " 00:00:00";
        SimpleDateFormat sdfStar = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
           return sdfStar.parse(dateStr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static Date getEndOfDay(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = "";
        if (Objects.isNull(date)) {
            Date now = new Date();
            formattedDate = sdf.format(now);
        }
        formattedDate = sdf.format(date);
        String dateStr = formattedDate + " 23:59:59";
        SimpleDateFormat sdfStar = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return sdfStar.parse(dateStr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 返回 yyyy-MM-dd HH:mm:ss
     * @param date
     * @return
     */
    public static String getSTrFromMs(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = "";
        if (Objects.isNull(date)) {
            Date now = new Date();
            formattedDate = sdf.format(now);
            return formattedDate;
        }
        formattedDate = sdf.format(date);
        return formattedDate;
    }

    public static boolean checkAllZero(int[] arr) {
        if (Objects.isNull(arr)) {
            return true;
        }
        for (int j : arr) {
            if (j != 0) {
                return false;
            }
        }
        return true;
    }

    public static String getDpStr(String value) {
        if (value.length() >= 3) {
            return "-24dp";
        }
        return "-20dp";
    }


}
