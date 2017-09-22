package com.candy.videoplayer.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;

import com.candy.videoplayer.R;
import com.candy.videoplayer.fragment.HomePageFragment;
import com.candy.videoplayer.fragment.VideoPlayFragment;


/**
 * ========================================================== <br>
 * <b>版权</b>：　　　candy 版权所有(c) 2017 <br>
 * <b>作者</b>：　　　candy<br>
 * <b>创建日期</b>：　17/6/13 <br>
 * <b>描述</b>：　　　<br>
 * <b>版本</b>：　    V1.0 <br>
 * <b>修订历史</b>：　<br>
 * ========================================================== <br>
 */
public class MainActivity extends AppCompatActivity {

    private final static String FRAGMENT_MAIN_TAG = "fragment_main_tag";
    private final static String FRAGMENT_VIDEO_TAG = "fragment_video_tag";
    private HomePageFragment mMainFragmet;
    private VideoPlayFragment mVideoPlayFragment;
    private Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setScreenPortrait();
        init();
        mIntent = getIntent();
        onIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mIntent = intent;
        onIntent(intent);
    }

    private void init() {
        mMainFragmet = new HomePageFragment();
        getFragmentManager().beginTransaction().add(
                R.id.fl_main, mMainFragmet, FRAGMENT_MAIN_TAG).commit();
    }

    private void setScreenPortrait() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
    }

    private void onIntent(Intent intent) {
        if (null == intent) {
            return;
        }
        String className = intent.getStringExtra(VideoPlayFragment.INTENT_CLASS_NAME);
        if (TextUtils.isEmpty(className)) {
            return;
        }
        if (className.equals(
                VideoPlayFragment.class.getName())) {
            mVideoPlayFragment = (VideoPlayFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_VIDEO_TAG);
            if (null == mVideoPlayFragment) {
                mVideoPlayFragment = new VideoPlayFragment();
                getSupportFragmentManager().beginTransaction().add(R.id.fl_video_play, mVideoPlayFragment, FRAGMENT_VIDEO_TAG).commit();
            } else {
                if (mVideoPlayFragment.isHidden()) {
                    getSupportFragmentManager().beginTransaction().show(mVideoPlayFragment).commit();
                }
                mVideoPlayFragment.getCandyVideoView().onIntent(intent);
            }
        }
    }

    public Intent getmIntent() {
        return mIntent;
    }

    public void removeVideoFragment() {
        FragmentTransaction fragmentTransaction;
        if (null != mVideoPlayFragment && mVideoPlayFragment.isAdded() && mVideoPlayFragment.isVisible()) {
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.remove(mVideoPlayFragment);
            fragmentTransaction.commitAllowingStateLoss();
            mVideoPlayFragment = null;
        }
    }

    @Override
    public void onBackPressed() {
        if (!onBackClick()) {
            super.onBackPressed();
        }
    }

    private boolean onBackClick() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_VIDEO_TAG);
        if (null != fragment && fragment instanceof VideoPlayFragment && fragment.isAdded()) {
            if (mVideoPlayFragment.onBackClick()) {
                return true;
            }
        }
        return false;
    }
}
