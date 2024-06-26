package com.tiger.yunda.ui.common;

import android.net.Uri;

import com.tiger.yunda.enums.CameraFileType;

import java.io.Serializable;

public class CameraContentBean implements Serializable {


    private CameraFileType type;


    private boolean problem;

    private String uri;

    private String filename;



    private int breakTypePosition;

    public CameraContentBean(CameraFileType type,  String uri, boolean problem, String filename, int breakTypePosition) {
        this.type = type;
        this.problem = problem;
        this.uri = uri;
        this.filename = filename;
        this.breakTypePosition = breakTypePosition;
    }

    public CameraContentBean(CameraFileType type, String uri, boolean problem, String filename) {
        this.type = type;
        this.uri = uri;
        this.problem = problem;
        this.filename = filename;
    }

    public int getBreakTypePosition() {
        return breakTypePosition;
    }

    public void setBreakTypePosition(int breakTypePosition) {
        this.breakTypePosition = breakTypePosition;
    }

    public CameraFileType getType() {
        return type;
    }

    public void setType(CameraFileType type) {
        this.type = type;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public boolean isProblem() {
        return problem;
    }

    public void setProblem(boolean problem) {
        this.problem = problem;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
