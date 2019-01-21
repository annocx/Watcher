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
 * created on: 2018/12/24 002420:07
 * packagename: cx.turam.com.watcher
 * projectname: LCDApplication
 * description:
 */
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


import android.os.Build.VERSION;
import android.os.Parcel;
import android.os.Parcelable;

public class WatcherConfig implements Parcelable {
    public static final String CONFIG_KEY = "config_key";
    public boolean isDebug = true;
    public boolean enableFps = true;
    public boolean enableMemory = true;
    public boolean enableShowCurrentActivity = true;
    public int seat = 8388691;

    public boolean enableSkipPermission() {
        return VERSION.SDK_INT < 24;
    }

    public WatcherConfig() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.isDebug ? (byte) 1 : (byte) 0);
        dest.writeByte(this.enableFps ? (byte) 1 : (byte) 0);
        dest.writeByte(this.enableMemory ? (byte) 1 : (byte) 0);
        dest.writeByte(this.enableShowCurrentActivity ? (byte) 1 : (byte) 0);
        dest.writeInt(this.seat);
    }

    protected WatcherConfig(Parcel in) {
        this.isDebug = in.readByte() != 0;
        this.enableFps = in.readByte() != 0;
        this.enableMemory = in.readByte() != 0;
        this.enableShowCurrentActivity = in.readByte() != 0;
        this.seat = in.readInt();
    }

    public static final Creator<WatcherConfig> CREATOR = new Creator<WatcherConfig>() {
        @Override
        public WatcherConfig createFromParcel(Parcel source) {
            return new WatcherConfig(source);
        }

        @Override
        public WatcherConfig[] newArray(int size) {
            return new WatcherConfig[size];
        }
    };
}

