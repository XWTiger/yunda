package com.tiger.yunda.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

public class FileUtil {


    public static String getRealPathFromUri(Context context, Uri uri) {
        Cursor cursor = null;
        String path = null;
        try {
            // 只查询媒体数据的数据列
            String[] projection = { MediaStore.MediaColumns.DATA };
            cursor = context.getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                // 获取数据列的索引，一般只有一个数据列，即索引为0
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                // 获取数据列的数据，即文件的实际路径
                path = cursor.getString(columnIndex);
            }
        } catch (Exception e) {
            // 处理异常情况
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return path;
    }
}
