package com.xxx.iot.common.observer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 观察者主题具体实现类
 * Created by Administrator on 2019/1/11.
 */

public class EventSubject implements EventSubjectInterface {

    private Map<EventType, ArrayList<EventObserver>> mEventObserversMap = new HashMap<>();
    private static volatile EventSubject instance;

    private EventSubject() {

    }

    public synchronized static EventSubject getInstance() {
        if (instance == null) {
            synchronized (EventSubject.class) {
                if (instance == null) {
                    instance = new EventSubject();
                }
            }
        }
        return instance;
    }

    @Override
    public void registerObserver(EventType eventType, EventObserver observer) {
        if (eventType == null || observer == null) {
            return;
        }
        synchronized (mEventObserversMap) {
            try {
                ArrayList<EventObserver> eventObservers = mEventObserversMap.get(eventType);
                if (eventObservers != null && eventObservers.contains(observer)) {
                    return;
                }
                if (eventObservers == null) {
                    eventObservers = new ArrayList<>();
                }

                eventObservers.add(observer);
                mEventObserversMap.put(eventType, eventObservers);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void removeObserver(EventType eventType, EventObserver observer) {
        if (eventType == null || observer == null) {
            return;
        }
        synchronized (mEventObserversMap) {
            try {
                if (!mEventObserversMap.containsKey(eventType)) {
                    return;
                }
                List<EventObserver> eventObserverList = mEventObserversMap.get(eventType);
                if (eventObserverList != null && eventObserverList.contains(observer)) {
                    eventObserverList.remove(observer);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void notifyObserver(EventType eventType, ThreadType threadType, String content) {

        if (eventType == null) {
            return;
        }
        try {
            if (!mEventObserversMap.containsKey(eventType)) {
                return;
            }
            List<EventObserver> eventObserverList = mEventObserversMap.get(eventType);
            if (eventObserverList != null) {
                for (EventObserver eventObserver : eventObserverList) {
                    eventObserver.dispatchChange(eventType, threadType, content);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
