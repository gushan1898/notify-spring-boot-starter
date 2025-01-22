package com.gushan.extension;

import com.gushan.sender.NotifySender;
import org.springframework.stereotype.Component;

@Component
public class CustomNotifySender implements NotifySender {

    @Override
    public void send(Exception exception) {
        // 实现自定义通知逻辑
        System.out.println("Custom notify sender: " + exception.getMessage());
    }
}
