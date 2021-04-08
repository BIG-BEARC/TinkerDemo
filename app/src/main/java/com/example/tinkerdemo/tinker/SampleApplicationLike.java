/*
 * Tencent is pleased to support the open source community by making Tinker available.
 *
 * Copyright (C) 2016 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.tinkerdemo.tinker;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.multidex.MultiDex;

import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.beta.interfaces.BetaPatchListener;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.tinker.entry.DefaultApplicationLike;
import com.tencent.tinker.lib.tinker.Tinker;

import java.util.Locale;


/**
  * Description : Application
  * ClassName : SampleApplicationLike
  * Author : Cybing
  * Date : 2020/8/28 11:06
 */
@SuppressWarnings("unused")
//@DefaultLifeCycle(application = "com.example.tinkertest.SampleApplication", flags = ShareConstants.TINKER_ENABLE_ALL, loadVerifyFlag = false)
@SuppressLint("LongLogTag")
public class SampleApplicationLike extends DefaultApplicationLike {
    private static final String TAG = "Tinker.SampleApplicationLike";

    public SampleApplicationLike(Application application, int tinkerFlags, boolean tinkerLoadVerifyFlag,
                                 long applicationStartElapsedTime, long applicationStartMillisTime, Intent tinkerResultIntent) {
        super(application, tinkerFlags, tinkerLoadVerifyFlag, applicationStartElapsedTime, applicationStartMillisTime, tinkerResultIntent);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // 这里实现SDK初始化，appId替换成你的在Bugly平台申请的appId
        // 调试时，将第三个参数改为true
        Beta.betaPatchListener = new BetaPatchListener() {// 补丁回调接口
            @Override
            public void onPatchReceived(String patchFile) {
                Log.i(TAG, "补丁下载地址" + patchFile);
            }

            @Override
            public void onDownloadReceived(long savedLength, long totalLength) {
                Log.i(TAG, String.format(Locale.getDefault(), "%s %d%%",
                        Beta.strNotificationDownloading,
                        (int) (totalLength == 0 ? 0 : savedLength * 100 / totalLength)));
            }

            @Override
            public void onDownloadSuccess(String msg) {
                Log.i(TAG, "补丁下载成功" + msg);
            }


            @Override
            public void onDownloadFailure(String msg) {
                Log.i(TAG, "补丁下载失败" + msg);
            }

            @Override
            public void onApplySuccess(String msg) {
                Log.i(TAG, "补丁应用成功" + msg);
            }

            @Override
            public void onApplyFailure(String msg) {
                Log.i(TAG, "补丁应用失败" + msg);
            }

            @Override
            public void onPatchRollback() {}
        };

        Bugly.init(getApplication(), "f44bdd3e1b", false);
        CrashReport.initCrashReport(getApplication(), "f44bdd3e1b", false);
    }



    /**
     * install multiDex before install tinker
     * so we don't need to put the tinker lib classes in the main dex
     *
     * @param base
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onBaseContextAttached(Context base) {
        super.onBaseContextAttached(base);
        MultiDex.install(base);
        //https://www.jianshu.com/p/eaae6afaaae3
        //https://www.jianshu.com/p/1aa836fa3733
//        TinkerManager.setTinkerApplicationLike(this);
//        TinkerManager.setUpgradeRetryEnable(true);
//        TinkerManager.installTinker(this);
//        Tinker.with(getApplication());
        // TinkerManager.installTinker(this); 替换成下面Bugly提供的方法
        Beta.installTinker(this);
    }
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void registerActivityLifecycleCallbacks(Application.ActivityLifecycleCallbacks callback) {
        getApplication().registerActivityLifecycleCallbacks(callback);
    }
}
