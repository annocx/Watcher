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
 * created on: 2018/12/24 002420:15
 * packagename: cx.turam.com.watcher.config
 * projectname: LCDApplication
 * description:
 */
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


import android.annotation.TargetApi;
import android.view.Choreographer;
import android.view.Choreographer.FrameCallback;



import java.util.concurrent.TimeUnit;

import cx.turam.watcher.config.IMonitorMethod;
import cx.turam.watcher.config.WatcherListener;

@TargetApi(16)
public class FpsMonitor implements FrameCallback, IMonitorMethod {
    private Choreographer mChoreographer = Choreographer.getInstance();
    private long mStartTime = 0L;
    private int mRenderCount = 0;
    private int mInterval = 500;
    private WatcherListener mListener;

    public FpsMonitor() {
    }

    public void start() {
        this.mChoreographer.postFrameCallback(this);
    }

    public void stop() {
        this.mChoreographer.removeFrameCallback(this);
    }

    public void setInterval(int time) {
        this.mInterval = time;
    }

    public void setListener(WatcherListener listener) {
        this.mListener = listener;
    }

    public void doFrame(long l) {
        long currentTimeMills = TimeUnit.MILLISECONDS.convert(l, TimeUnit.NANOSECONDS);
        if (this.mStartTime > 0L) {
            long waitTime = currentTimeMills - this.mStartTime;
            ++this.mRenderCount;
            if (waitTime > (long)this.mInterval) {
                int fps = (int)((long)(this.mRenderCount * 1000) / waitTime);
                this.mStartTime = currentTimeMills;
                this.mRenderCount = 0;
                this.mListener.post((double)fps);
            }
        } else {
            this.mStartTime = currentTimeMills;
        }

        this.mChoreographer.postFrameCallback(this);
    }
}

