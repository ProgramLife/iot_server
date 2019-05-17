package com.xxx.iot.common;

/**
 * Created by Administrator on 2019/1/8.
 */

public class BaseThred extends Thread {

    public volatile boolean runFlag;

    public BaseThred() {
        this.runFlag = false;
    }

    public boolean isRunFlag() {
        return runFlag & !this.isInterrupted();
    }

    public void setRunFlag(boolean runFlag) {
        this.runFlag = runFlag;
    }

    public void startThread() {
        this.runFlag = true;
        this.start();
    }

    public void stopThread() {
        this.runFlag = false;
        this.interrupt();
    }
}
