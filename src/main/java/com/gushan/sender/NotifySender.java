package com.gushan.sender;

/**
 * 通知发送器接口
 * <p>
 * 定义通知发送的统一接口，不同的通知渠道需要实现该接口
 */
public interface NotifySender {
    /**
     * 发送异常通知
     * 
     * @param exception 需要通知的异常对象
     */
    void send(Exception exception);
}
