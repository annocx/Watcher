package cx.turam.watcher.config;

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
 * created on: 2018/12/24 002420:00
 * packagename: cx.turam.com.watcher.config
 * projectname: LCDApplication
 * description:
 */

import android.app.Activity;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.os.Bundle;



import java.util.Stack;

import cx.turam.watcher.*;
import cx.turam.watcher.function.Action1;

public class AppBackground implements ActivityLifecycleCallbacks {
    private static final String TAG = AppBackground.class.getSimpleName();
    private static AppBackground sInstance;
    private int mCount = 0;
    private boolean mIsBackground = false;
    private Stack<Activity> mActivityStack = new Stack();
    private Action1<String> mResumeAction;

    public static AppBackground init(Application app) {
        if (sInstance == null) {
            sInstance = new AppBackground(app);
        }

        return sInstance;
    }

    public static AppBackground getInstance() {
        assert sInstance != null;

        return sInstance;
    }

    private AppBackground(Application app) {
        app.registerActivityLifecycleCallbacks(this);
    }

    public void onActivityCreated(Activity activity, Bundle bundle) {
        this.mActivityStack.add(activity);
    }

    public void onActivityStarted(Activity activity) {
        ++this.mCount;
        if (this.mIsBackground) {
            this.mIsBackground = false;
            Watcher.getInstance().start(activity.getApplicationContext());
        }

    }

    public void onActivityResumed(Activity activity) {
        if (this.mResumeAction != null) {
            this.mResumeAction.call(activity.getClass().getSimpleName());
        }

    }

    public void onActivityPaused(Activity activity) {
    }

    public void onActivityStopped(Activity activity) {
        --this.mCount;
        if (!this.mIsBackground && this.mCount < 0) {
            this.mIsBackground = true;
           Watcher.getInstance().stop(activity.getApplicationContext());
        }

    }

    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
    }

    public void onActivityDestroyed(Activity activity) {
        this.mActivityStack.remove(activity);
    }

    public void setResumeAction(Action1<String> resumeAction) {
        this.mResumeAction = resumeAction;
    }

    public Activity getCurActivity() {
        return this.mActivityStack.isEmpty() ? null : (Activity)this.mActivityStack.get(this.mActivityStack.size() - 1);
    }
}
