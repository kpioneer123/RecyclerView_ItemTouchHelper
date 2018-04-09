package utils;

import android.app.Application;
import android.content.Context;

import com.blankj.utilcode.util.Utils;

/**
 * Created by Xionghu on 2018/4/8.
 * Desc:
 */

public class MyApplication  extends Application{
    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);
        MyApplication.context=getApplicationContext();
    }
    public static Context getAppContext() {
        return MyApplication.context;
    }
}
