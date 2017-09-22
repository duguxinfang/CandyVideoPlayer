package com.candy.videoplayer.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.candy.videoplayer.R;
import com.candy.videoplayer.adapter.HomePageAdapter;
import com.candy.videoplayer.controller.VideoController;
import com.candy.videoplayer.entity.MainEntity;
import com.candy.videoplayer.utils.UIUtils;
import com.candy.videoplayer.view.CandyVideoView;

import java.util.ArrayList;

/**
 * ========================================================== <br>
 * <b>版权</b>：　　　音悦台 版权所有(c) 2017 <br>
 * <b>作者</b>：　　　dongxu.tian@yinyuetai.com<br>
 * <b>创建日期</b>：　17/6/15 <br>
 * <b>描述</b>：　　　<br>
 * <b>版本</b>：　    V1.0 <br>
 * <b>修订历史</b>：　<br>
 * ========================================================== <br>
 */

public class HomePageFragment extends Fragment {

    private View mRootView;
    private FrameLayout mFullPlayView;
    private RecyclerView mRecyclerView;
    private HomePageAdapter mAdapter;
    private ArrayList<MainEntity> mListData;
    private HomePageAdapter.ClickListenerImpl mClickListenr;
    private CandyVideoView mCandyVideoView;
    private RelativeLayout mImageLayout;
    private int mLastposition = -1;
    private int mLastItemPosition, mFirstItemPosition;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.frag_homepage, container, false);
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();
        initData();
        showContent();
        initVideoListener();
    }

    private void init() {
        mFullPlayView = (FrameLayout) mRootView.findViewById(R.id.fl_layout);
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.rccyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new HomePageAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initData() {
        mListData = new ArrayList<>();
        String[] urlList = getResources().getStringArray(R.array.video_url_list);
        String[] imgList = getResources().getStringArray(R.array.thumb_url_list);
        for (int i = 0; i < urlList.length; i++) {
            MainEntity entity = new MainEntity();
            entity.setVideoUrl(urlList[i]);
            entity.setThumbUrl(imgList[i]);
            mListData.add(entity);
        }
    }

    private void showContent() {
        mAdapter.setData(mListData);
        mAdapter.notifyDataSetChanged();
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
    public void onDestroy() {
        super.onDestroy();
        onDestroyCandyVideoView();
    }

    private void initVideoListener() {
        mClickListenr = new HomePageAdapter.ClickListenerImpl() {
            @Override
            public void playInItem(int position, RelativeLayout layout) {
                if (null == layout) {
                    return;
                }
                if (mLastposition != -1) {
                    onDestroyCandyVideoView();
                    if(null != mCandyVideoView) {
                        mCandyVideoView.removeVideoFragment();
                    }
                }
                onDestroyCandyVideoView();
                showItemView();
                View view = mRecyclerView.findViewHolderForAdapterPosition(position).itemView;
                mImageLayout = (RelativeLayout) view.findViewById(R.id.rl_img_layout);
                mImageLayout.setVisibility(View.GONE);
                FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.fl_video_layout);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, UIUtils.getScreenWidth() * 9 / 16);
                frameLayout.setLayoutParams(params);
                MainEntity entity = mAdapter.getItem(position);
                if (null != entity) {
                    mCandyVideoView = new CandyVideoView(getActivity());
                    mCandyVideoView.sendParams(getActivity(), entity.getVideoUrl());
                    mCandyVideoView.setPlayInItem();
                    frameLayout.removeAllViews();
                    frameLayout.addView(mCandyVideoView);
                    mLastposition = position;
                }
            }

            @Override
            public void playInDetail(int position) {
                if (position == mLastposition) {
                    mImageLayout.setVisibility(View.VISIBLE);
                    VideoController.getInstance().setPlayType(VideoController.PLAY_BY_ITEM);
                    VideoController.getInstance().setCandyVideoView(mCandyVideoView);
                    VideoPlayFragment.launch(getActivity(), "https://storage.googleapis.com/exoplayer-test-media-1/gen-3/screens/dash-vod-single-segment/video-avc-baseline-480.mp4");
                } else {
                    onDestroyCandyVideoView();
                    VideoController.getInstance().setPlayType(VideoController.PLAY_BY_DEFAULT);
                    VideoPlayFragment.launch(getActivity(), "https://storage.googleapis.com/exoplayer-test-media-1/gen-3/screens/dash-vod-single-segment/video-avc-baseline-480.mp4");
                }
            }
        };
        mAdapter.setListener(mClickListenr);

        mRecyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {

            /**
             * 当recyclerView的item添加到屏幕时
             * @param view
             */
            @Override
            public void onChildViewAttachedToWindow(View view) {

            }

            /**
             * 当item离开屏幕时
             * @param view
             */
            @Override
            public void onChildViewDetachedFromWindow(View view) {
                if (null == mCandyVideoView) {
                    return;
                }
                if(mCandyVideoView.isMiniVideo()) {
                    return;
                }
                int index = mRecyclerView.getChildAdapterPosition(view);
                if(index == mLastposition) {
                    showItemView();
                    VideoController.getInstance().setCandyVideoView(mCandyVideoView);
                    VideoController.getInstance().setPlayType(VideoController.PLAY_BY_LISTSCROLL);
                    VideoPlayFragment.launch(getActivity(), "");
                 }


            }
        });

        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
                if (layoutManager instanceof LinearLayoutManager) {
                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                    //获取最后一个可见view的position
                    mLastItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                    //获取第一个可见view的position
                    mFirstItemPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
                }
            }
        });
    }

    private void showItemView() {
        if (null != mImageLayout) {
            mImageLayout.setVisibility(View.VISIBLE);
        }
    }

    private void onDestroyCandyVideoView() {
        if(null != mCandyVideoView){
            mCandyVideoView.releasePlayer();
            showItemView();
            mLastposition = -1;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if(null != mCandyVideoView) {
                FrameLayout frameLayout = (FrameLayout) mCandyVideoView.getParent();
                if(null != frameLayout) {
                    frameLayout.removeAllViews();
                }
                mCandyVideoView.initViewLandscape();
                mFullPlayView.addView(mCandyVideoView);
            }

        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            if(null != mCandyVideoView) {
                FrameLayout fullPlayView = (FrameLayout) mCandyVideoView.getParent();
                if(null != fullPlayView) {
                    fullPlayView.removeAllViews();
                }
                mCandyVideoView.initViewPortrait();
                View view = mRecyclerView.findViewHolderForAdapterPosition(mLastposition).itemView;
                FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.fl_video_layout);
                if(null != frameLayout) {
                    frameLayout.addView(mCandyVideoView);
                }
            }
        }
    }
}
