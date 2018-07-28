package com.jld.obdserialport.event_msg;

public class MediaMessage {

    private int flag;
    private double uid;
    private String fileName;
    private String fileType;

    public static final int EVENT_MSG_PHOTO = 1;
    public static final int EVENT_MSG_VIDEO = 2;

    public MediaMessage(int flag) {
        this.flag = flag;
    }

    public int getFlag() {
        return flag;
    }

    public double getUid() {
        return uid;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileType() {
        return fileType;
    }
}
