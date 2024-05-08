package com.tiger.yunda.utils;





import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static android.os.Environment.DIRECTORY_PICTURES;

import android.app.DownloadManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.tiger.yunda.dao.ResourceLocationDao;
import com.tiger.yunda.data.model.OperationResource;
import com.tiger.yunda.entity.ResourceLocationEntity;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Objects;

public class DownLoadUtil {

    /**
     *
     * @param downloadUrl 现在地址
     * @param context
     * @param fileName 保存文件名称
     */
    public static Long downLoad(String downloadUrl, Context context, String fileName) {

        //String imagePath = String.format("%s/%s",Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS).toPath(), "Yunda.apk");
        String imagePath = String.format("%s/%s",context.getFilesDir().toPath(), fileName);
       /* Intent intent1 = OpenFileUtil.openFile(context, imagePath);
        if (Objects.nonNull(intent1)) {
            context.startActivity(intent1);
        }
        return 0L;*/
        //校验网络
        boolean available = NetworkUtil.isNetworkAvailable(context);
        if (!available) {
            Toast.makeText(context, "网络异常,请确认后重试!", Toast.LENGTH_SHORT).show();
            return -1L;
        }
        String serviceString = Context.DOWNLOAD_SERVICE;
        DownloadManager downloadManager;
        downloadManager = (DownloadManager)context.getApplicationContext().getSystemService(serviceString);

        Uri uri = Uri.parse(downloadUrl);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle(fileName +" 下载");
        File file = new File(imagePath);
        request.setDestinationUri(Uri.fromFile(file));
        // 在下载过程中通知栏会一直显示该下载的Notification，在下载完成后该Notification会继续显示，
        // 直到用户点击该Notification或者消除该Notification
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        long myDownloadReference = downloadManager.enqueue(request);

        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (myDownloadReference == reference) {
                    DownloadManager.Query myDownloadQuery = new DownloadManager.Query();
                    myDownloadQuery.setFilterById(reference);

                    Cursor myDownload = downloadManager.query(myDownloadQuery);
                    if (Objects.nonNull(myDownload) && myDownload.moveToFirst()) {


                       /* String imagePath = String.format("%s/%s",Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS).toPath(), fileName);*/

                        if (StringUtils.isNotBlank(imagePath)) {
                            Intent intent1 = OpenFileUtil.openFile(context, imagePath);
                            if (Objects.nonNull(intent1)) {
                                context.startActivity(intent1);
                            }
                        }

                        // TODO Do something with the file.
                        Log.d("xiaweihu", fileName + " : " + imagePath);
                    }
                }
            }
        };
        context.getApplicationContext().registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED);
        return myDownloadReference;
    }

    public static void cancelDownload(Long referenceId, Context context) {
        String serviceString = Context.DOWNLOAD_SERVICE;
        DownloadManager downloadManager;
        downloadManager = (DownloadManager)context.getApplicationContext().getSystemService(serviceString);
        downloadManager.remove(referenceId);
    }

    public static String getFileUriString(Long referenceId, Context context) {
        String serviceString = Context.DOWNLOAD_SERVICE;
        DownloadManager downloadManager;
        downloadManager = (DownloadManager)context.getApplicationContext().getSystemService(serviceString);
        DownloadManager.Query myDownloadQuery = new DownloadManager.Query();
        myDownloadQuery.setFilterById(referenceId);

        Cursor myDownload = downloadManager.query(myDownloadQuery);
        if (Objects.nonNull(myDownload) && myDownload.moveToFirst()) {

            int fileUriIdx =
                    myDownload.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);

            String fileUri = myDownload.getString(fileUriIdx);
            int index = fileUri.lastIndexOf("/");
            if (index > 0) {
                String fileName = fileUri.substring(index);
                return String.format("%s%s",Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS).toPath(), fileName);
            } else {
                return "";
            }

        }

        return "";
    }

    public static Long downLoadAndSave(String downloadUrl, Context context, String fileName, ResourceLocationEntity resourceLocationEntity, ResourceLocationDao resourceLocationDao) {
        //校验网络
        boolean available = NetworkUtil.isNetworkAvailable(context);
        if (!available) {
            Toast.makeText(context, "网络异常,请确认后重试!", Toast.LENGTH_SHORT).show();
            return -1L;
        }
        String serviceString = Context.DOWNLOAD_SERVICE;
        DownloadManager downloadManager;
        downloadManager = (DownloadManager)context.getApplicationContext().getSystemService(serviceString);

        /*Uri uri = Uri.parse("http://developer.android.com/shareables/icon_templates-v4.0.zip");*/
        Uri uri = Uri.parse(downloadUrl);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle(fileName +" 下载");
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                fileName);
        // 在下载过程中通知栏会一直显示该下载的Notification，在下载完成后该Notification会继续显示，
        // 直到用户点击该Notification或者消除该Notification
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        long myDownloadReference = downloadManager.enqueue(request);

        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (myDownloadReference == reference) {
                    DownloadManager.Query myDownloadQuery = new DownloadManager.Query();
                    myDownloadQuery.setFilterById(reference);

                    Cursor myDownload = downloadManager.query(myDownloadQuery);
                    if (Objects.nonNull(myDownload) && myDownload.moveToFirst()) {

                        String imagePath = String.format("%s/%s",Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS).toPath(), fileName);
                        resourceLocationEntity.setLocation(imagePath);
                        resourceLocationDao.insert(resourceLocationEntity);
                        if (StringUtils.isNotBlank(imagePath)) {
                            Intent intent1 = OpenFileUtil.openFile(context, imagePath);
                            if (Objects.nonNull(intent1)) {
                                context.startActivity(intent1);
                            }
                        }

                        // TODO Do something with the file.
                        Log.d("xiaweihu", fileName + " : " + imagePath);
                    }
                }
            }
        };
        context.getApplicationContext().registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED);
        return myDownloadReference;
    }

}
