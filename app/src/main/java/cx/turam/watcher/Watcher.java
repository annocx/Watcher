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
 * created on: 2018/12/24 002420:07
 * packagename: cx.turam.com.watcher
 * projectname: LCDApplication
 * description:
 */
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;

import cx.turam.watcher.config.AppBackground;
import cx.turam.watcher.config.WatcherConfig;

public class Watcher {
    private WatcherConfig mWatcherConfig;
    private boolean mHasStarted;

    private Watcher() {
        this.mHasStarted = false;
    }

    public static Watcher getInstance() {
        return SingletonHolder.Instance;
    }
    private static class SingletonHolder {
        private static final Watcher Instance = new Watcher();
    }
    public Watcher setWatcherConfig(WatcherConfig watcherConfig) {
        this.mWatcherConfig = watcherConfig;
        return this;
    }

    public void start(Context context) {
        if (!this.mHasStarted) {
            if (this.mWatcherConfig == null) {
                this.mWatcherConfig = new WatcherConfig();
            }

            if (this.mWatcherConfig.isDebug) {
                if (!this.mWatcherConfig.enableSkipPermission() && !Settings.canDrawOverlays(context)) {
                    Log.e("Watcher", "!!! ---> Can't start Watcher : permission denied for window type");
                } else {
                    AppBackground.init((Application)context.getApplicationContext());
                    Intent intent = new Intent(context,WatcherService.class);
                    intent.putExtra("config_key", this.mWatcherConfig);
                    context.startService(intent);
                    this.mHasStarted = true;
                }
            }
        }
    }

    public void stop(Context context) {
        if (this.mHasStarted) {
            this.mHasStarted = false;
            context.stopService(new Intent(context,WatcherService.class));
        }

    }
}

