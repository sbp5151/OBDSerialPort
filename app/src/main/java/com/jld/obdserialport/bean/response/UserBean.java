package com.jld.obdserialport.bean.response;

public class UserBean {

    private int age;
    private int id;
    private String nickName;
    private String openId;
    private String sex;

    public int getAge() {
        return age;
    }

    public int getId() {
        return id;
    }

    public String getNickName() {
        return nickName;
    }

    public String getOpenId() {
        return openId;
    }

    public String getSex() {
        return sex;
    }

    @Override
    public String toString() {
        return "UserBean{" +
                "age=" + age +
                ", id=" + id +
                ", nickName='" + nickName + '\'' +
                ", openId='" + openId + '\'' +
                ", sex='" + sex + '\'' +
                '}';
    }
}
