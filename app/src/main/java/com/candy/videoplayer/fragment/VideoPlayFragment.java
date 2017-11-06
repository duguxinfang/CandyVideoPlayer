package com.candy.videoplayer.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.ViewUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.candy.videoplayer.R;
import com.candy.videoplayer.activity.MainActivity;
import com.candy.videoplayer.controller.VideoController;
import com.candy.videoplayer.view.CandyVideoView;
import com.google.android.exoplayer2.util.LongArray;
import com.google.android.exoplayer2.util.Util;


/**
 * ========================================================== <br>
 * <b>版权</b>：　　　candy(c) 2017 <br>
 * <b>作者</b>：　　　candy<br>
 * <b>创建日期</b>：　17/6/15 <br>
 * <b>描述</b>：　　　<br>
 * <b>版本</b>：　    V1.0 <br>
 * <b>修订历史</b>：　<br>
 * ========================================================== <br>
 */

public class VideoPlayFragment extends Fragment  {

    public static String INTENT_CLASS_NAME = "intent_class_name";
    private View mRootView;
    private FrameLayout mMainLayout;
    private CandyVideoView mCandyVideoView;

    public static void launch(Context context, String url) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(INTENT_CLASS_NAME, VideoPlayFragment.class.getName());
        intent.setData(Uri.parse(url));
        context.startActivity(intent);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.frag_video_player, container, false);
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        mMainLayout = (FrameLayout) mRootView.findViewById(R.id.fl_layout);
        if(VideoController.getInstance().getPlayType() == VideoController.PLAY_BY_DEFAULT) {
            mCandyVideoView = (CandyVideoView) mRootView.findViewById(R.id.candy_video_view);
            mCandyVideoView.initViewSurface(getActivity(), ((MainActivity)getActivity()).getmIntent());
            mCandyVideoView.setPlayByDefault();
        } else if (VideoController.getInstance().getPlayType() == VideoController.PLAY_BY_ITEM) {
            mCandyVideoView = VideoController.getInstance().getCandyVideoView();
            mCandyVideoView.setPlayInItem();
            FrameLayout frameLayout = (FrameLayout) mCandyVideoView.getParent();
            if(null != frameLayout){
                frameLayout.removeAllViews();
            }
            mMainLayout.addView(mCandyVideoView);
        } else if(VideoController.getInstance().getPlayType() == VideoController.PLAY_BY_LISTSCROLL) {
            mCandyVideoView = VideoController.getInstance().getCandyVideoView();
            FrameLayout frameLayout = (FrameLayout) mCandyVideoView.getParent();
            if(null != frameLayout){
                frameLayout.removeAllViews();
            }
            mMainLayout.addView(mCandyVideoView);
            mCandyVideoView.setMiniVideoViewByItem();
        }

    }

    public boolean onBackClick() {
        return mCandyVideoView.removeVideoFragment();
    }

    public CandyVideoView getCandyVideoView() {
        return mCandyVideoView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            mCandyVideoView.releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            mCandyVideoView.releasePlayer();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(null != mCandyVideoView)
            mCandyVideoView.setConfigureChanged();
    }
}
