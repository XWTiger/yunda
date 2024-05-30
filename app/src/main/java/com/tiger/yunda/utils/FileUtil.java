package com.tiger.yunda.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;

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


    /**
     * vie
     * @param uri
     * @param context
     * @param video
     * @return
     */
    public static File uri2File(Uri uri, Context context, boolean video) {
        context.getFilesDir();
        ContentResolver contentResolver = context.getContentResolver();
        String img_path;
        String[] proj = {MediaStore.Images.Media._ID,MediaStore.Images.Media.MIME_TYPE,MediaStore.Images.Media.DISPLAY_NAME};
        String selection = MediaStore.Images.Media.DISPLAY_NAME + "= ?";
        String[] args = new String[]{"like '%.jpg%'"};
        Cursor actualimagecursor =  contentResolver.query(MediaStore.Images.Media.INTERNAL_CONTENT_URI, proj, selection, args, null);
        if (actualimagecursor == null) {
            img_path = uri.getPath();
        } else {

            int mimeTypeIndex = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE);
            actualimagecursor.moveToFirst();
            img_path = actualimagecursor
                    .getString(mimeTypeIndex);
        }
        File file = new File(img_path);
        return file;
    }


    public static String getFileStr(Uri uri, Context context) {
        // 获取ContentResolver
        ContentResolver contentResolver = context.getContentResolver();

        // 查询Uri对应的数据
        Cursor cursor = contentResolver.query(uri, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            // 假设你的Uri是指向媒体库的数字ID
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
            String filePath = cursor.getString(columnIndex);
          /*  int rindex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.RELATIVE_PATH);
            String relative = cursor.getString(rindex);*/

            // 使用文件路径
            Log.i("FilePath", filePath);
            cursor.close();
            return filePath;

        }
        return "";
    }
}
