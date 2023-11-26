package com.xpbiomed.xppda;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.os.Bundle;

import org.xutils.x;


/*
Author by nezaking
2023年11月26日，此代码的作用是因为xutils3必须要求写在appliction里面


 */
public class MainAppliction extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this); //初始化xutils
    }
}
