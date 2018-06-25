package com.jld.obdserialport.bean;

public class BaseBean {

    private int result;
    private String msg;
    private int postFailNum = 0;//数据上传失败次数

    public int getResult() {
        return result;
    }

    public String getMsg() {
        return msg;
    }

    public int getPostFailNum() {
        return postFailNum;
    }

    public void setPostFailNum(int postFailNum) {
        this.postFailNum = postFailNum;
    }

}
