package cx.turam.watcher;

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
 * created on: 2018/12/24 002420:14
 * packagename: cx.turam.com.watcher.config
 * projectname: LCDApplication
 * description:
 */
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;


import cx.turam.com.watcher.R;
import cx.turam.watcher.config.AppBackground;
import cx.turam.watcher.config.WatcherConfig;
import cx.turam.watcher.config.WatcherListener;
import cx.turam.watcher.function.Action1;
import cx.turam.watcher.monitor.FpsMonitor;
import cx.turam.watcher.monitor.MemoryMonitor;

import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicInteger;

public class WatcherService extends Service {
    private final DecimalFormat mFpsFormat = new DecimalFormat("#0.0' fps'"), mFormatPercent = new DecimalFormat("##0.0");
    private final DecimalFormat mMemoryFormat = new DecimalFormat("#0.00' MB'");
    private WindowManager mWindowManager;
    private View mStageView;
    private TextView mTvCpu;
    private TextView mTvFps;
    private TextView mTvMemory;
    private TextView mTvCurrentActivity;
    private FpsMonitor mFpsMonitor;
    private MemoryMonitor mMemoryMonitor;
    private Handler mHandler;
    private boolean mHasInitialized = false;
    private WatcherConfig mWatcherConfig;
    private AtomicInteger mAtomicInteger = new AtomicInteger(0);
    private ServiceReader mSR;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @SuppressLint("NewApi")
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mSR = ((ServiceReader.ServiceReaderDataBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            mSR = null;
        }
    };


    public WatcherService() {
    }

    public void onCreate() {
        super.onCreate();
        this.mWindowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        this.mHandler = new Handler(this.getMainLooper());
        startService(new Intent(this, ServiceReader.class));

    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return START_NOT_STICKY;
        } else {
            bindService(new Intent(this, ServiceReader.class), mServiceConnection, 0);
            this.mWatcherConfig = (WatcherConfig) intent.getParcelableExtra("config_key");
            if (this.mWatcherConfig != null && !this.mHasInitialized) {
                this.initView();

                initCpu();
                if (this.mWatcherConfig.enableFps) {
                    this.initFps();
                }

                if (this.mWatcherConfig.enableMemory) {
                    this.initMemory();
                }

                if (this.mWatcherConfig.enableShowCurrentActivity) {
                    this.initCurrentActivity();
                }

                this.mHasInitialized = true;
            }

            return START_NOT_STICKY;
        }
    }


    private void initView() {
        LayoutParams layoutParams = new LayoutParams();
        layoutParams.width = -2;
        layoutParams.height = -2;
        layoutParams.type = this.mWatcherConfig.enableSkipPermission() ? 2005 : 2002;
        layoutParams.flags = 184;
        layoutParams.format = -3;
        layoutParams.gravity = this.mWatcherConfig.seat;
        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout parent = new LinearLayout(this);
        parent.setOrientation(LinearLayout.VERTICAL);
        parent.setBackgroundColor(Color.parseColor("#60ffffff"));
        mStageView = inflater.inflate(R.layout.watcher_stage, parent);
        this.mWindowManager.addView(this.mStageView, layoutParams);
    }

    private void initCpu() {
        // synchronized (mTvCpu) {
        if (mTvCpu == null)
            mTvCpu = (TextView) mStageView.findViewById(R.id.tv_cpu);
        if (mSR != null) {
            if (!mSR.getCPUTotalP().isEmpty()) {
                mTvCpu.setText(String.format("cpu使用率:%s%%/自用:%s%%",
                        mFormatPercent.format(mSR.getCPUTotalP().get(0)),
                        mFormatPercent.format(mSR.getCPUAMP().get(0))));
            }
        }
        // mTvCpu.setText(mSR.getCPUTotalP() + ));
        // }
    }

    private void initFps() {
        mTvFps = (TextView) mStageView.findViewById(R.id.tv_fps);
        mFpsMonitor = new FpsMonitor();
        mFpsMonitor.setListener(new WatcherListener() {
            @Override
            public void post(double value) {
                if (mTvFps != null) {
                    mTvFps.setText(mFpsFormat.format(value));
                }
                if (mAtomicInteger.addAndGet(1) > 11) {
                    mAtomicInteger = new AtomicInteger(0);
                    initCpu();
                }
            }
        });
        mFpsMonitor.start();
    }

    private void initMemory() {
        mTvMemory = (TextView) mStageView.findViewById(R.id.tv_memory);
        mMemoryMonitor = new MemoryMonitor((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE), getPackageName());
        mMemoryMonitor.setListener(new WatcherListener() {
            @Override
            public void post(final double value) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mTvMemory != null) {
                            mTvMemory.setText(String.format("自用%s/总内存:%sMB/可用内存%s",
                                    mMemoryFormat.format(value),
                                    //H.getTotalMemory(WatcherService.this),
                                    mSR==null?0:mFormatPercent.format(mSR.getMemTotal()/1024),
                                    getAvailMemory(WatcherService.this)
                                   // , mFormatPercent.format(mSR.getMemoryAM().get(0))
                            ));
                        }
                    }
                });
            }
        });
        mMemoryMonitor.start();
    }

    private void initCurrentActivity() {
        mTvCurrentActivity = (TextView) mStageView.findViewById(R.id.tv_current_activity);
        Activity a = AppBackground.getInstance().getCurActivity();
        if (a != null) {
            mTvCurrentActivity.setText(a.getClass().getSimpleName());
            AppBackground.getInstance().setResumeAction(new Action1<String>() {
                @Override
                public void call(String name) {
                    mTvCurrentActivity.setText(name);
                }
            });
        }
    }
    public  String getAvailMemory(Context context) {// 获取android当前可用内存大小 

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        //mi.availMem; 当前系统的可用内存
        return Formatter.formatFileSize(context, mi.availMem);// 将获取的内存大小规格化 
    }
    private void stop() {
        if (mWatcherConfig.enableFps) {
            mFpsMonitor.stop();
        }
        if (mWatcherConfig.enableMemory) {
            mMemoryMonitor.stop();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHasInitialized) {
            mWindowManager.removeView(mStageView);
            stop();
        }
    }
}
