package com.jld.obdserialport.bean.response;

import com.jld.obdserialport.bean.response.JpushBase;

public class JpushMediaBean extends JpushBase {

    private String obdId;
    private String uid;
    private String fileName;
    private int fileType;
    private int isFront;
    private int videoDuration;

    //    public JpushMediaBean(int fileType) {
//        this.fileType = fileType;
//    }
//
//    public JpushMediaBean(String obdId, String uid, String fileName, int fileType, int flag, int videoDuration) {
//        this.obdId = obdId;
//        this.uid = uid;
//        this.fileName = fileName;
//        this.fileType = fileType;
//        this.flag = flag;
//        this.videoDuration = videoDuration;
//    }
    public String getObdId() {
        return obdId;
    }

    public String getUid() {
        return uid;
    }

    public String getFileName() {
        return fileName;
    }

    public int getFileType() {
        return fileType;
    }

    public int getVideoDuration() {
        return videoDuration;
    }

    public int getIsFront() {
        return isFront;
    }

    @Override
    public String toString() {
        return "JpushMediaBean{" +
                "obdId='" + obdId + '\'' +
                ", uid='" + uid + '\'' +
                ", fileName='" + fileName + '\'' +
                ", fileType=" + fileType +
                ", isFront=" + isFront +
                ", videoDuration=" + videoDuration +
                ", flag=" + flag +
                '}';
    }
}
