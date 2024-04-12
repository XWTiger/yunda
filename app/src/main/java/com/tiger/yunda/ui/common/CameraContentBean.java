package com.tiger.yunda.ui.common;

import android.net.Uri;

import com.tiger.yunda.enums.CameraFileType;

import java.io.Serializable;

public class CameraContentBean implements Serializable {


    private CameraFileType type;


    private boolean problem;

    private Uri uri;


    public CameraContentBean(CameraFileType type, Uri uri, boolean problem) {
        this.type = type;
        this.uri = uri;
        this.problem = problem;
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

    public boolean isProblem() {
        return problem;
    }

    public void setProblem(boolean problem) {
        this.problem = problem;
    }
}
