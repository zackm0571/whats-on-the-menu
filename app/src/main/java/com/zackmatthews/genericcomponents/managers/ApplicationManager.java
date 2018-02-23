package com.zackmatthews.genericcomponents.managers;

import android.app.Application;

/**
 * Created by zmatthews on 2/23/18.
 */

public class ApplicationManager extends Application {
    public static final boolean isAdmin = false;
    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
