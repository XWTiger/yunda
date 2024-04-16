package com.tiger.yunda.utils;




import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

public class TimeUtil {


    public static String getDateStringFromMs(Long ms) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        date.setTime(ms);
       return sdf.format(date);
    }
}
