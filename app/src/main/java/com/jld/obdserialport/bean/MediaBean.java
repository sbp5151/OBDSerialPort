package com.jld.obdserialport.bean;

public class MediaBean extends JpushBase {

    private String fileName;
    private int fileType;
    private String obdId;
    private String uid;
    private int videoDuration;

//    public MediaBean(int fileType) {
//        this.fileType = fileType;
//    }
//
//    public MediaBean(String obdId, String uid, String fileName, int fileType, int flag, int videoDuration) {
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

    @Override
    public String toString() {
        return "MediaBean{" +
                "obdId='" + obdId + '\'' +
                ", uid='" + uid + '\'' +
                ", fileName='" + fileName + '\'' +
                ", fileType=" + fileType +
                ", videoDuration=" + videoDuration +
                '}';
    }
}
