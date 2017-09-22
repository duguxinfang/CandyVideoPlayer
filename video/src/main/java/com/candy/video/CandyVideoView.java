package com.candy.video;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.text.TextRenderer;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.util.Util;

import java.net.CookieManager;
import java.util.List;

/**
 * ========================================================== <br>
 * <b>版权</b>：　　　音悦台 版权所有(c) 2017 <br>
 * <b>作者</b>：　　　dongxu.tian@yinyuetai.com<br>
 * <b>创建日期</b>：　17/8/10 <br>
 * <b>描述</b>：　　　<br>
 * <b>版本</b>：　    V1.0 <br>
 * <b>修订历史</b>：　<br>
 * ========================================================== <br>
 */

public class CandyVideoView extends FrameLayout {

    private Context mContext;
    private AspectRatioFrameLayout mAspectRatioFrameLayout;
    private TextureView mTextureView;
    private SimpleExoPlayer mPlayer;

    private SimpleExoPlayer mSimpleExoPlayer;
    private ComponentListener mComponentListener;
    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
//    private static final CookieManager DEFAULT_COOKIE_MANAGER;
    private DefaultTrackSelector trackSelector;

    public CandyVideoView(Context context) {
        super(context);
    }

    public CandyVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CandyVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.candy_video_view, this);
        mAspectRatioFrameLayout = (AspectRatioFrameLayout) findViewById(R.id.exo_content_frame);
        mTextureView = new TextureView(context);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mTextureView.setLayoutParams(params);
        mAspectRatioFrameLayout.addView(mTextureView);
    }

    private void initPlayer() {
        mComponentListener = new ComponentListener();
        DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(mContext,
                null, DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON);
        TrackSelection.Factory adaptiveTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);
        trackSelector = new DefaultTrackSelector(adaptiveTrackSelectionFactory);
        mPlayer = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector);
        mPlayer.setPlayWhenReady(true);
        mPlayer.setVideoTextureView(mTextureView);
        mPlayer.setVideoListener(mComponentListener);
        mPlayer.addListener(mComponentListener);
    }

    private final class ComponentListener implements SimpleExoPlayer.VideoListener,
            ExoPlayer.EventListener {

        // SimpleExoPlayer.VideoListener implementation

        @Override
        public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees,
                                       float pixelWidthHeightRatio) {
            if (mAspectRatioFrameLayout != null) {
                float aspectRatio = height == 0 ? 1 : (width * pixelWidthHeightRatio) / height;
                mAspectRatioFrameLayout.setAspectRatio(aspectRatio);
            }
        }

        @Override
        public void onRenderedFirstFrame() {

        }

        @Override
        public void onTracksChanged(TrackGroupArray tracks, TrackSelectionArray selections) {
//            updateForCurrentTrackSelections();
        }

        // ExoPlayer.EventListener implementation

        @Override
        public void onLoadingChanged(boolean isLoading) {
            // Do nothing.
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

        }

        @Override
        public void onPlayerError(ExoPlaybackException e) {
            // Do nothing.
        }

        @Override
        public void onPositionDiscontinuity() {
            // Do nothing.
        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
            // Do nothing.
        }

        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest) {
            // Do nothing.
        }

    }

}
