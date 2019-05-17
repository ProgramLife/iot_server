package com.xxx.iot.tcp;

import io.netty.channel.Channel;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by chen on 2018/12/18
 * tcp channel 连接信息
 */
@Component
public class ChannelRepository {

    private Map<String, Channel> channelCache = new ConcurrentHashMap<String, Channel>();

    public ChannelRepository put(String key, Channel value) {
        channelCache.put(key, value);
        return this;
    }

    public Channel get(String key) {
        return channelCache.get(key);
    }

    public void remove(String key) {
        this.channelCache.remove(key);
    }

    public int size() {
        return this.channelCache.size();
    }

    public String getKeyByValue(Channel value) {
        Set setKey = channelCache.entrySet();
        String key = "";
        Iterator<Map.Entry<String, Channel>> iterator = setKey.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Channel> entry = iterator.next();
            if (entry.getValue().equals(value)) {
                key = entry.getKey();
                break;
            }
        }
        return key;
    }

    public void reflash() {
        Set setKey = channelCache.entrySet();
        Channel channel;
        Iterator<Map.Entry<String, Channel>> iterator = setKey.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Channel> entry = iterator.next();
            channel = entry.getValue();
            if (channel == null || !channel.isActive()) {
                remove(entry.getKey());
            }
        }
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, Channel> entry : channelCache.entrySet()) {
            sb.append("Key = " + entry.getKey() + ", Value = " + entry.getValue() + " ");
        }
        return sb.toString();
    }
}
