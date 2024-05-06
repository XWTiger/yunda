package com.tiger.yunda.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import androidx.core.content.FileProvider;

import java.io.File;
import java.util.Locale;

public class OpenFileUtil {

    public static Intent openFile(Context context, String filePath) {

        File file = new File(filePath);
        if (!file.exists())
            return null;
        /* 取得扩展名 */
        String end = file.getName().substring(file.getName().lastIndexOf(".") + 1, file.getName().length()).toLowerCase(Locale.getDefault());
        /* 依扩展名的类型决定MimeType */
        if (isAudio(filePath)) {
            return getAudioFileIntent(context, file);
        } else if (isVideo(filePath)) {
            return getVideoFileIntent(context, file);
        } else if (end.equals("jpg") || end.equals("gif") || end.equals("png") || end.equals("jpeg") || end.equals("bmp")) {
            return getImageFileIntent(filePath);
        } else if (isApk(filePath)) {
            return getApkFileIntent(context, file);
        } else if (isPpt(filePath)) {
            return getPptFileIntent(context, file);
        } else if (isExcel(filePath)) {
            return getExcelFileIntent(context, file);
        } else if (isDoc(filePath)) {
            return getWordFileIntent(context, file);
        } else if (isPdf(filePath)) {
            return getPdfFileIntent(context, file);
        } else if (isChm(filePath)) {
            return getChmFileIntent(context, file);
        } else if (isTxt(filePath)) {
            return getTextFileIntent(context, file);
        } else if (isZip(filePath)) {
            return getZipIntent(context, file);
        } else {
            return getAllIntent(context, file);
        }
    }

    // Android获取一个用于打开APK文件的intent
    public static Intent getAllIntent(Context context, File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        String authority = context.getPackageName() + ".fileprovider";
        Uri uri = FileProvider.getUriForFile(context, authority, file);
        intent.setDataAndType(uri, "*/*");
        return intent;
    }

    // Android获取一个用于打开APK文件的intent
    public static Intent getApkFileIntent(Context context, File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //判读版本是否在7.0以上
        if (Build.VERSION.SDK_INT >= 24) {
            String authority = context.getPackageName() + ".fileprovider";
            Uri uri = FileProvider.getUriForFile(context, authority, file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
        return intent;
    }

    // Android获取一个用于打开VIDEO文件的intent
    public static Intent getVideoFileIntent(Context context, File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.putExtra("oneshot", 0);
//        intent.putExtra("configchange", 0);
//        Uri uri = Uri.fromFile(new File(param));
        String authority = context.getPackageName() + ".fileprovider";
        Uri uri = FileProvider.getUriForFile(context, authority, file);
        intent.setDataAndType(uri, "video/*");
        return intent;
    }

    // Android获取一个用于打开AUDIO文件的intent
    public static Intent getAudioFileIntent(Context context, File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        String authority = context.getPackageName() + ".fileprovider";
        Uri uri = FileProvider.getUriForFile(context, authority, file);
        intent.setDataAndType(uri, "audio/*");
        return intent;
    }

    // Android获取一个用于打开Html文件的intent
    public static Intent getHtmlFileIntent(String param) {

        Uri uri = Uri.parse(param).buildUpon().encodedAuthority("com.android.htmlfileprovider").scheme("content").encodedPath(param).build();
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setDataAndType(uri, "text/html");
        return intent;
    }

    // Android获取一个用于打开图片文件的intent
    public static Intent getImageFileIntent(String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "image/*");
        return intent;
    }

    // Android获取一个用于打开PPT文件的intent
    public static Intent getPptFileIntent(Context context, File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory("android.intent.category.DEFAULT");
        String authority = context.getPackageName() + ".fileprovider";
        Uri uri = FileProvider.getUriForFile(context, authority, file);
        intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        return intent;
    }

    // Android获取一个用于打开Excel文件的intent
    public static Intent getExcelFileIntent(Context context, File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory("android.intent.category.DEFAULT");
        String authority = context.getPackageName() + ".fileprovider";
        Uri uri = FileProvider.getUriForFile(context, authority, file);
        intent.setDataAndType(uri, "application/vnd.ms-excel");
        return intent;
    }

    // Android获取一个用于打开Word文件的intent
    public static Intent getWordFileIntent(Context context, File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory("android.intent.category.DEFAULT");
        String authority = context.getPackageName() + ".fileprovider";
        Uri uri = FileProvider.getUriForFile(context, authority, file);
        intent.setDataAndType(uri, "application/msword");
        return intent;
    }

    // Android获取一个用于打开CHM文件的intent
    public static Intent getChmFileIntent(Context context, File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
//    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory("android.intent.category.DEFAULT");
        String authority = context.getPackageName() + ".fileprovider";
        Uri uri = FileProvider.getUriForFile(context, authority, file);
        intent.setDataAndType(uri, "application/x-chm");
        return intent;
    }

    // Android获取一个用于打开文本文件的intent
    public static Intent getTextFileIntent(Context context, File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory("android.intent.category.DEFAULT");
//    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        String authority = context.getPackageName() + ".fileprovider";
        Uri uri = FileProvider.getUriForFile(context, authority, file);
        intent.setDataAndType(uri, "text/plain");
        return intent;
    }

    public static Intent getZipIntent(Context context, File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory("android.intent.category.DEFAULT");
//    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        String authority = context.getPackageName() + ".fileprovider";
        Uri uri = FileProvider.getUriForFile(context, authority, file);
        intent.setDataAndType(uri, "application/x-compress");
        return intent;
    }

    // Android获取一个用于打开PDF文件的intent
    public static Intent getPdfFileIntent(Context context, File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory("android.intent.category.DEFAULT");
//    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        String authority = context.getPackageName() + ".fileprovider";
        Uri uri = FileProvider.getUriForFile(context, authority, file);
        intent.setDataAndType(uri, "application/pdf");
        return intent;
    }

    public static boolean isVideo(String filePath) {
        if (TextUtils.isEmpty(filePath)) return false;
        String ext = getFileExtension(filePath);
        return isVideoType(MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext));
    }

    static boolean isAudio(String filePath) {
        if (TextUtils.isEmpty(filePath)) return false;
        String ext = getFileExtension(filePath);
        if (TextUtils.isEmpty(ext)) return false;
        return ext.equals("m4a")
                || ext.equals("mp3")
                || ext.equals("mid")
                || ext.equals("xmf")
                || ext.equals("ogg")
                || ext.equals("wav");
    }

    static boolean isTxt(String filePath) {
        if (TextUtils.isEmpty(filePath)) return false;
        String ext = getFileExtension(filePath);
        if (TextUtils.isEmpty(ext)) return false;
        return ext.equals("txt");
    }

    static boolean isDoc(String filePath) {
        if (TextUtils.isEmpty(filePath)) return false;
        String ext = getFileExtension(filePath);
        if (TextUtils.isEmpty(ext)) return false;
        return ext.equals("doc") || ext.equals("docx");
    }

    static boolean isPpt(String filePath) {
        if (TextUtils.isEmpty(filePath)) return false;
        String ext = getFileExtension(filePath);
        if (TextUtils.isEmpty(ext)) return false;
        return ext.equals("ppt");
    }

    static boolean isExcel(String filePath) {
        if (TextUtils.isEmpty(filePath)) return false;
        String ext = getFileExtension(filePath);
        if (TextUtils.isEmpty(ext)) return false;
        return ext.equals("xls") || ext.equals("xlsx");
    }

    static boolean isApk(String filePath) {
        if (TextUtils.isEmpty(filePath)) return false;
        String ext = getFileExtension(filePath);
        if (TextUtils.isEmpty(ext)) return false;
        return ext.equals("apk");
    }

    static boolean isPdf(String filePath) {
        if (TextUtils.isEmpty(filePath)) return false;
        String ext = getFileExtension(filePath);
        if (TextUtils.isEmpty(ext)) return false;
        return ext.equals("pdf");
    }

    static boolean isChm(String filePath) {
        if (TextUtils.isEmpty(filePath)) return false;
        String ext = getFileExtension(filePath);
        if (TextUtils.isEmpty(ext)) return false;
        return ext.equals("chm");
    }

    static boolean isZip(String filePath) {
        if (TextUtils.isEmpty(filePath)) return false;
        String ext = getFileExtension(filePath);
        if (TextUtils.isEmpty(ext)) return false;
        return ext.equals("zip");
    }

    private static boolean isVideoType(String mimeType) {
        if (mimeType == null) return false;
        return mimeType.equals("mpeg")
                || mimeType.equals("mp4")
                || mimeType.equals("ts")
                || mimeType.equals("avi");
    }

    public static String getFileExtension(String filePath) {
        int index = filePath.lastIndexOf(".");
        return filePath.substring(index +1);
    }

}
