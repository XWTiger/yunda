package com.tiger.yunda.data;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Environment;

import com.tiger.yunda.enums.CameraFileType;
import com.tiger.yunda.ui.common.CameraContentBean;
import com.tiger.yunda.ui.common.CameraFragment;
import com.tiger.yunda.utils.CollectionUtil;
import com.tiger.yunda.utils.FileUtil;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import okhttp3.MediaType;
import okhttp3.RequestBody;

//上报故障对象

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BreakDownInfo {


    private String subtaskId;

    private String trainLocationId;

    //故障类型
    private Integer type;
    private int typePosition;

    private String desc;

    private Boolean discretion;


    //故障文件
    private List<CameraContentBean> files = new ArrayList<>();

    private String handleDesc;

    private List<CameraContentBean> handleFiles = new ArrayList<>();




    //false 故障 true 恢复拍照
    private boolean typeOfCameraAction;

    public Map<String, RequestBody>  getBreakFilesUri(Context context) {

        Map<String, RequestBody> bodyMap = new HashMap<>();
        if (CollectionUtil.isEmpty(files)) {
            return bodyMap;
        }

        files.forEach(cameraContentBean ->  {
            if (cameraContentBean.getType() == CameraFileType.IMAGE) {
                File file = null;
                file = new File(cameraContentBean.getUri());


                RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpg"), file);
                //注意：file就是与服务器对应的key,后面filename是服务器得到的文件名
                bodyMap.put("Files\"; filename=\"" + cameraContentBean.getFilename(), requestFile);
            }
            if (cameraContentBean.getType() == CameraFileType.VIDEO) {
                File file = null;
                file = new File(cameraContentBean.getUri());
                //Uri uri = Uri.parse(cameraContentBean.getUri());
                //File file = FileUtil.uri2File(uri, context);
                RequestBody requestFile = RequestBody.create(MediaType.parse("video/mp4"), file);
                //注意：file就是与服务器对应的key,后面filename是服务器得到的文件名
                bodyMap.put("Files\"; filename=\"" + cameraContentBean.getFilename(), requestFile);
            }

        });
        return bodyMap;
    }

    public Map<String, RequestBody>  getHandleFilesUri() {
        Map<String, RequestBody> bodyMap = new HashMap<>();
        if (CollectionUtil.isEmpty(files)) {
            return bodyMap;
        }

        handleFiles.forEach(cameraContentBean ->  {

            if (cameraContentBean.getType() == CameraFileType.IMAGE) {
                File file = new File(cameraContentBean.getUri());
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpg"), file);
                //注意：file就是与服务器对应的key,后面filename是服务器得到的文件名
                bodyMap.put("HandleFiles\"; filename=\"" + cameraContentBean.getFilename() , requestFile);
            }
            if (cameraContentBean.getType() == CameraFileType.VIDEO) {
                File file = new File(cameraContentBean.getUri());
                RequestBody requestFile = RequestBody.create(MediaType.parse("video/mp4"), file);
                //注意：file就是与服务器对应的key,后面filename是服务器得到的文件名
                bodyMap.put("HandleFiles\"; filename=\"" + cameraContentBean.getFilename() , requestFile);
            }

        });
        return bodyMap;
    }

    public void clear() {
        subtaskId = null;
        trainLocationId = null;
        typePosition = 0;
        desc = null;
        discretion = false;
        files.clear();
        handleDesc = null;
        handleFiles.clear();
        typeOfCameraAction = false;
    }

}
