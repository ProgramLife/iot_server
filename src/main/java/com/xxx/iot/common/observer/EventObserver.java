package com.xxx.iot.common.observer;

/**
 * 抽象观察者对象
 * Created by Administrator on 2019/1/11.
 */

public interface EventObserver {

    public void dispatchChange(EventType eventType, ThreadType threadType, String data);
}
