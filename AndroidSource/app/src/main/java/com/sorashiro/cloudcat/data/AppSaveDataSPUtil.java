package com.sorashiro.cloudcat.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.securepreferences.SecurePreferences;
import com.sorashiro.cloudcat.application.MyApplication;

import java.lang.ref.WeakReference;

/**
 * @author Sora
 * @date 2016.11.7
 * <p>
 * A class to use SharedPreferences conveniently.
 * 一个方便使用SharedPreferences的类。
 */

public class AppSaveDataSPUtil {

    private static final String SOMETHING = "something";
    private static final String FIRST_IN  = "first_in";
    private static final String USER_NAME = "user_name";
    private static final String PASS_WORD = "pass_word";
    private static WeakReference<Context>   sContext;
    private static SharedPreferences        sSharedPreferences;
    private static SharedPreferences.Editor sEditor;

    private static AppSaveDataSPUtil instance = null;

    private AppSaveDataSPUtil() {
        init(MyApplication.getInstance());
    }

    private void init(Context context) {
        if (sContext != null) {
            sContext = null;
        }
        sContext = new WeakReference<>(context);
        sSharedPreferences = new SecurePreferences(context);
        sEditor = sSharedPreferences.edit();
        sEditor.apply();
    }

    public static AppSaveDataSPUtil getInstance() {
        if (instance == null) {
            synchronized (AppSaveDataSPUtil.class) {
                if (instance == null) {
                    instance = new AppSaveDataSPUtil();
                }
            }
        }
        return instance;
    }

    public boolean getSomething() {
        return sSharedPreferences.getBoolean(SOMETHING, true);
    }

    public void setSomething(boolean something) {
        sEditor.putBoolean(SOMETHING, something);
        sEditor.commit();
    }

    public boolean getIfFirstIn() {
        return sSharedPreferences.getBoolean(FIRST_IN, true);
    }

    public void setFirstIn(boolean ifFirstIn) {
        sEditor.putBoolean(FIRST_IN, ifFirstIn);
        sEditor.commit();
    }

    public String getUserName() {
        return sSharedPreferences.getString(USER_NAME, "");
    }

    public void setUserName(String userName) {
        sEditor.putString(USER_NAME, userName);
        sEditor.commit();
    }

    public String getPassWord() {
        return sSharedPreferences.getString(PASS_WORD, "");
    }

    public void setPassWord(String passWord) {
        sEditor.putString(PASS_WORD, passWord);
        sEditor.commit();
    }

}
