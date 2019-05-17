package com.xxx.iot.common.observer;

/**
 * 观察者主题
 * Created by Administrator on 2019/1/11.
 */

public interface EventSubjectInterface {

    /**
     * 注册观察者
     **/
    public void registerObserver(EventType eventType, EventObserver observer);

    /**
     * 注销观察者
     **/
    public void removeObserver(EventType eventType, EventObserver observer);

    /**
     * 通知注册的观察者数据或者UI更新
     **/
    public void notifyObserver(EventType eventType, ThreadType threadType, final String content);
}
