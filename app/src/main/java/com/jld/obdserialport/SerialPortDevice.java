package com.jld.obdserialport;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 串口设备类
 * 功能：串口连接、获取串口输入输出流
 */
public class SerialPortDevice {

    private String mPath;//串口路径
    private int mBaudRate;//波特率
    //    private int mFlags;//串口标志
    private FileDescriptor mFileDescriptor;//串口文件描述符

    public SerialPortDevice(String path, int baudRate) {
        this.mPath = path;
        this.mBaudRate = baudRate;
    }

    /**
     * 串口连接
     *
     * @return
     */
    public boolean connect() {
        mFileDescriptor = open(mPath, mBaudRate, 0);
        return mFileDescriptor != null;
    }

    public void disConnect() {
        if (mFileDescriptor != null)
            close();
    }

    /**
     * 获取串口输入流
     *
     * @return
     */
    public InputStream getInputStream() {
        if (mFileDescriptor == null)
            return null;
        return new FileInputStream(mFileDescriptor);
    }

    /**
     * 获取串口输出流
     *
     * @return
     */
    public OutputStream getOutputStream() {
        if (mFileDescriptor == null)
            return null;
        return new FileOutputStream(mFileDescriptor);
    }

    static {
        System.loadLibrary("SerialPort");//Android.mk中LOCAL_MODULE
    }

    public native static String helloJni();

    private native void close();

    private native FileDescriptor open(String path, int baudrate, int flags);
}
