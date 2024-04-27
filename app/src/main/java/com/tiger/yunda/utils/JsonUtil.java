package com.tiger.yunda.utils;

import android.content.Context;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tiger.yunda.data.model.ErrorResult;

public class JsonUtil {

    public static ErrorResult getObject(String errMsg, Context context) {
        Gson gson = new GsonBuilder().create();
        ErrorResult errorResult = gson.fromJson(errMsg, ErrorResult.class);
        Toast.makeText(context, errorResult.getTitle(), Toast.LENGTH_SHORT).show();
        return errorResult;
    }
}
