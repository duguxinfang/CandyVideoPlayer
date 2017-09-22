package com.candy.videoplayer;

import android.app.Application;
import android.content.Context;

import com.candy.videoplayer.utils.UIUtils;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;

/**
 * ========================================================== <br>
 * <b>版权</b>：　　　音悦台 版权所有(c) 2017 <br>
 * <b>作者</b>：　　　dongxu.tian@yinyuetai.com<br>
 * <b>创建日期</b>：　17/6/14 <br>
 * <b>描述</b>：　　　<br>
 * <b>版本</b>：　    V1.0 <br>
 * <b>修订历史</b>：　<br>
 * ========================================================== <br>
 */

public class CandyApplication extends Application {

    private static Context mContext;
    private static CandyApplication mApp;
    protected String userAgent;

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
        mContext = getApplicationContext();
        UIUtils.initDip2px(mContext);
        userAgent = Util.getUserAgent(this, "ExoPlayerDemo");
    }

    public static CandyApplication getApplication() {
        return mApp;
    }

    public static Context getContext(){
        return mContext;
    }

    public DataSource.Factory buildDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultDataSourceFactory(this, bandwidthMeter,
                buildHttpDataSourceFactory(bandwidthMeter));
    }

    public HttpDataSource.Factory buildHttpDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultHttpDataSourceFactory(userAgent, bandwidthMeter);
    }

    public boolean useExtensionRenderers() {
        return BuildConfig.FLAVOR.equals("withExtensions");
    }
}
