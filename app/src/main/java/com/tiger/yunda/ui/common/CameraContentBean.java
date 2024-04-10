package com.tiger.yunda.ui.common;

import android.net.Uri;

import com.tiger.yunda.enums.CameraFileType;

import java.io.Serializable;

public class CameraContentBean implements Serializable {


    private CameraFileType type;

    private Uri uri;


    public CameraContentBean(CameraFileType type, Uri uri) {
        this.type = type;
        this.uri = uri;
    }

    public CameraFileType getType() {
        return type;
    }

    public void setType(CameraFileType type) {
        this.type = type;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
}
