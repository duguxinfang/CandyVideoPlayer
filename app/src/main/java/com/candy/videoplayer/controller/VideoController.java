package com.candy.videoplayer.controller;

import com.candy.videoplayer.fragment.VideoPlayFragment;
import com.candy.videoplayer.view.CandyVideoView;

/**
 * ========================================================== <br>
 * <b>版权</b>：　　　音悦台 版权所有(c) 2017 <br>
 * <b>作者</b>：　　　dongxu.tian@yinyuetai.com<br>
 * <b>创建日期</b>：　17/8/22 <br>
 * <b>描述</b>：　　　<br>
 * <b>版本</b>：　    V1.0 <br>
 * <b>修订历史</b>：　<br>
 * ========================================================== <br>
 */

public class VideoController {

    private static VideoController mInstance;

    public static int PLAY_BY_DEFAULT = 1;
    public static int PLAY_IN_ITEM = PLAY_BY_DEFAULT + 1;
    public static int PLAY_BY_ITEM = PLAY_IN_ITEM + 1;
    public static int PLAY_BY_LISTSCROLL = PLAY_BY_ITEM + 1;

    private int mPlayType;
    private CandyVideoView mCandyVideoView;

    public static VideoController getInstance() {
        if(null == mInstance){
            synchronized (VideoController.class) {
                if(null == mInstance) {
                    mInstance = new VideoController();
                }
            }
        }
        return mInstance;
    }

    public void setPlayType(int playType) {
        this.mPlayType = playType;
    }

    public int getPlayType() {
        return mPlayType;
    }

    public void setCandyVideoView(CandyVideoView candyVideoView) {
        this.mCandyVideoView = candyVideoView;
    }

    public CandyVideoView getCandyVideoView() {
        return mCandyVideoView;
    }

}
