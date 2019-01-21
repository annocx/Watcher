package cx.turam.watcher.monitor;

/**
 * author: annocx
 * ___           ___            ___              _____         _____          _ _
 * /  /\         /__/\          /__/\            /  /::\       /  /::|        /__/\
 * /  /::\        \  \:\         \  \:\         /  /:/\:\     /  /:/          |: :|
 * /  /:/\:\        \  \:\         \  \:\      /  /:/  \:\   /  /:/           |: :|
 * /  /:/~/::\    ____\__\:\     ___ \__\:\   /__/:/    \_\  |__/:/        ___|: :|___
 * /__/:/ /:/\:\ /__/::::::::\  /__/::::::::\ \  \:\    /:/  |\:\     __  /__/::::::::\
 * \  \:\/:/__\/ \  \:\~~\~~\/  \  \:\~~\~~\/  \  \::  /:/   \  \::  /:/  \~~\:\~~\~~\/
 * \  \::/        \  \:\  ~~~    \  \:\  ~~~    \  \:\/:/     \  \:\/:/       |: :|
 * \  \:\          \  \:\         \  \:\         \  \/;/       \  \/;/        |: :|
 * \__\:\           \__\/          \__\/          \__\/         \ : /         \_:_\
 * <p>
 * created on: 2018/12/24 002420:16
 * packagename: cx.turam.com.watcher.config
 * projectname: LCDApplication
 * description:
 */
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.os.Debug.MemoryInfo;


import java.util.Iterator;
import java.util.List;

import cx.turam.watcher.config.IMonitorMethod;
import cx.turam.watcher.config.WatcherListener;

public class MemoryMonitor implements IMonitorMethod {
    private int mInterval = 500;
    private long mStartTime = 0L;
    private boolean isFinish = false;
    private WatcherListener mListener;
    private ActivityManager mActivityManager;
    private String mPackageName;
    private RunningAppProcessInfo mRunningAppProcessInfo;

    public MemoryMonitor(ActivityManager mActivityManager, String packageName) {
        this.mActivityManager = mActivityManager;
        this.mPackageName = packageName;
    }

    public void setListener(WatcherListener listener) {
        this.mListener = listener;
    }

    public void start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isFinish) {
                    long currentTime = System.currentTimeMillis();
                    long diff = currentTime - mStartTime;
                    if (diff > mInterval) {
                        mListener.post(getRunningAppProcessInfo());
                        mStartTime = System.currentTimeMillis();
                    }
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void stop() {
        this.isFinish = true;
    }

    public void setInterval(int time) {
        this.mInterval = time;
    }

    private double getRunningAppProcessInfo() {
        if (this.mRunningAppProcessInfo != null) {
            return this.getMemSize(this.mRunningAppProcessInfo) / 1024.0D;
        } else {
            List<RunningAppProcessInfo> appProcessList = this.mActivityManager.getRunningAppProcesses();
            Iterator var4 = appProcessList.iterator();

            double memSize;
            RunningAppProcessInfo appProcessInfo;
            String processName;
            do {
                if (!var4.hasNext()) {
                    return -1.0D;
                }

                appProcessInfo = (RunningAppProcessInfo)var4.next();
                processName = appProcessInfo.processName;
                memSize = this.getMemSize(appProcessInfo);
            } while(!processName.equals(this.mPackageName));

            this.mRunningAppProcessInfo = appProcessInfo;
            return memSize / 1024.0D;
        }
    }

    private double getMemSize(RunningAppProcessInfo appProcessInfo) {
        int pid = appProcessInfo.pid;
        int[] memPid = new int[]{pid};
        MemoryInfo[] memoryInfo = this.mActivityManager.getProcessMemoryInfo(memPid);
        return (double)memoryInfo[0].dalvikPrivateDirty;
    }
}

