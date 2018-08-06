package com.jld.obdserialport.utils;

import com.jld.obdserialport.event_msg.TestDataMessage;

import org.greenrobot.eventbus.EventBus;

public class TestLogUtil {

    private static TestDataMessage message;

    public static void log(String msg) {

        if (message == null)
            message = new TestDataMessage();
        message.setTestMessage(msg);
        EventBus.getDefault().post(message);
    }
}
