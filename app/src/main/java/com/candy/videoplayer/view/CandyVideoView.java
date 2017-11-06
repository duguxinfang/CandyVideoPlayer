package com.candy.videoplayer.view;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.candy.videoplayer.CandyApplication;
import com.candy.videoplayer.R;
import com.candy.videoplayer.activity.MainActivity;
import com.candy.videoplayer.utils.UIUtils;
import com.candy.videoplayer.view.drag.YoutubeLayout;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerCtrlScreenListener;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlaybackControlView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;

import java.lang.ref.WeakReference;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

/**
 * ========================================================== <br>
 * <b>版权</b>：　　　candy(c) 2017 <br>
 * <b>作者</b>：　　　candy<br>
 * <b>创建日期</b>：　17/8/21 <br>
 * <b>描述</b>：　　　<br>
 * <b>版本</b>：　    V1.0 <br>
 * <b>修订历史</b>：　<br>
 * ========================================================== <br>
 */

public class CandyVideoView extends FrameLayout implements View.OnClickListener, ExoPlayer.EventListener,
        PlaybackControlView.VisibilityListener, YoutubeLayout.DragStateCallback {

    private Activity mContext;
    private View mRootView;
    private SimpleExoPlayerView mSimpleExoPlayerView;
    private LinearLayout mLinearlayout;

    private YoutubeLayout mYoutubeLayout;

    private DataSource.Factory mediaDataSourceFactory;
    private SimpleExoPlayer player;
    private DefaultTrackSelector trackSelector;
    private Handler mainHandler;
    private boolean shouldAutoPlay;
    private int resumeWindow;
    private long resumePosition;

    private Uri[] uris;

    private boolean isMiniVideo = false;
    public static float VIDEO_HBW = 0.56f; //视频高比宽

    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private static final CookieManager DEFAULT_COOKIE_MANAGER;

    static {
        DEFAULT_COOKIE_MANAGER = new CookieManager();
        DEFAULT_COOKIE_MANAGER.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
    }

    public CandyVideoView(Context context) {
        super(context);
    }

    public CandyVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CandyVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void sendParams(Activity context, String url) {
        uris = new Uri[]{Uri.parse(url)};
        initView(context);
    }

    public void initViewSurface(Activity context, Intent intent) {
        uris = new Uri[]{intent.getData()};
        initView(context);
    }

    private void initView(Activity context) {
        mContext = context;
        mContext.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        UIUtils.initDip2px(mContext);
        if (CookieHandler.getDefault() != DEFAULT_COOKIE_MANAGER) {
            CookieHandler.setDefault(DEFAULT_COOKIE_MANAGER);
        }
        shouldAutoPlay = true;
        clearResumePosition();
        mediaDataSourceFactory = buildDataSourceFactory(true);
        mainHandler = new Handler();
        if (CookieHandler.getDefault() != DEFAULT_COOKIE_MANAGER) {
            CookieHandler.setDefault(DEFAULT_COOKIE_MANAGER);
        }

        mRootView = LayoutInflater.from(context).inflate(R.layout.vw_candy_video_view, this);
        mSimpleExoPlayerView = (SimpleExoPlayerView) mRootView.findViewById(R.id.player_view);
        mSimpleExoPlayerView.setControllerVisibilityListener(this);
        mSimpleExoPlayerView.setCtrlScreenListener(new CtrlScreenListener());
        mSimpleExoPlayerView.requestFocus();

        mLinearlayout = (LinearLayout) mRootView.findViewById(R.id.ll_layout);

        mYoutubeLayout = (YoutubeLayout) mRootView.findViewById(R.id.youtubelayout);
        mYoutubeLayout.setyDragStateCallback(this);

        initializePlayer(null, false);
    }

    private void getIntentValue(Intent intent) {
        if (null == intent) {
            return;
        }
        uris = new Uri[]{intent.getData()};
    }

    public void setPlayInItem() {
        mYoutubeLayout.setyDragStateCallback(null);
    }

    public void setPlayByDefault() {
        mYoutubeLayout.setyDragStateCallback(this);
    }

    public void setMiniVideoViewByItem() {
        mLinearlayout.setVisibility(View.GONE);
        setPlayByDefault();
        mHandler.sendEmptyMessageDelayed(MSG_MIX_VIDEO, 100L);
    }

    public void initializePlayer(Intent intent, boolean dataFromIntent) {
        boolean needNewPlayer = player == null;
        if (needNewPlayer) {
            DrmSessionManager<FrameworkMediaCrypto> drmSessionManager = null;

            boolean preferExtensionDecoders = false;
            @DefaultRenderersFactory.ExtensionRendererMode int extensionRendererMode =
                    (CandyApplication.getApplication()).useExtensionRenderers()
                            ? (preferExtensionDecoders ? DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER
                            : DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON)
                            : DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF;
            DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(mContext,
                    drmSessionManager, extensionRendererMode);

            TrackSelection.Factory adaptiveTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);
            trackSelector = new DefaultTrackSelector(adaptiveTrackSelectionFactory);

            player = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector);
            player.addListener(this);

            mSimpleExoPlayerView.setPlayer(player);
            player.setPlayWhenReady(shouldAutoPlay);
        }
        if (dataFromIntent) {
            getIntentValue(intent);
        }

        if (null == uris) {
            return;
        }
        if (Util.maybeRequestReadExternalStoragePermission((Activity) mContext, uris)) {
            // The player will be reinitialized if the permission is granted.
            return;
        }
        MediaSource[] mediaSources = new MediaSource[uris.length];
        for (int i = 0; i < uris.length; i++) {
            mediaSources[i] = buildMediaSource(uris[i], null);
        }
        MediaSource mediaSource = mediaSources.length == 1 ? mediaSources[0]
                : new ConcatenatingMediaSource(mediaSources);
        boolean haveResumePosition = resumeWindow != C.INDEX_UNSET;
        if (haveResumePosition) {
            player.seekTo(resumeWindow, resumePosition);
        }
        player.prepare(mediaSource, !haveResumePosition, false);

    }

    private MediaSource buildMediaSource(Uri uri, String overrideExtension) {
        int type = TextUtils.isEmpty(overrideExtension) ? Util.inferContentType(uri)
                : Util.inferContentType("." + overrideExtension);
        switch (type) {
            case C.TYPE_SS:
                return new SsMediaSource(uri, buildDataSourceFactory(false),
                        new DefaultSsChunkSource.Factory(mediaDataSourceFactory), mainHandler, null);
            case C.TYPE_DASH:
                return new DashMediaSource(uri, buildDataSourceFactory(false),
                        new DefaultDashChunkSource.Factory(mediaDataSourceFactory), mainHandler, null);
            case C.TYPE_HLS:
                return new HlsMediaSource(uri, mediaDataSourceFactory, mainHandler, null);
            case C.TYPE_OTHER:
                return new ExtractorMediaSource(uri, mediaDataSourceFactory, new DefaultExtractorsFactory(),
                        mainHandler, null);
            default: {
                throw new IllegalStateException("Unsupported type: " + type);
            }
        }
    }

    private void clearResumePosition() {
        resumeWindow = C.INDEX_UNSET;
        resumePosition = C.TIME_UNSET;
    }

    /**
     * Returns a new DataSource factory.
     *
     * @param useBandwidthMeter Whether to set {@link #BANDWIDTH_METER} as a listener to the new
     *                          DataSource factory.
     * @return A new DataSource factory.
     */
    private DataSource.Factory buildDataSourceFactory(boolean useBandwidthMeter) {
        return CandyApplication.getApplication()
                .buildDataSourceFactory(useBandwidthMeter ? BANDWIDTH_METER : null);
    }

    /**
     * Returns a new HttpDataSource factory.
     *
     * @param useBandwidthMeter Whether to set {@link #BANDWIDTH_METER} as a listener to the new
     *                          DataSource factory.
     * @return A new HttpDataSource factory.
     */
    private HttpDataSource.Factory buildHttpDataSourceFactory(boolean useBandwidthMeter) {
        return CandyApplication.getApplication()
                .buildHttpDataSourceFactory(useBandwidthMeter ? BANDWIDTH_METER : null);
    }

    public void releasePlayer() {
        if (player != null) {
            shouldAutoPlay = player.getPlayWhenReady();
            player.release();
            player = null;
            trackSelector = null;
        }
    }

    public SimpleExoPlayer getPlayer() {
        return player;
    }

    public void onIntent(Intent intent) {
        if (null == intent) {
            return;
        }
        initializePlayer(intent, true);
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onVisibilityChange(int visibility) {

    }

    public void setConfigureChanged() {
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mLinearlayout.setVisibility(View.GONE);
            mHandler.sendEmptyMessageDelayed(MSG_LANDSCAPE, 200l);
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mLinearlayout.setVisibility(View.VISIBLE);
            mHandler.sendEmptyMessageDelayed(MSG_PORTRAIT, 200l);
        }

    }

    public void initViewLandscape() {
        if (Build.VERSION.SDK_INT >= 14)
            toggleHideyBar(true);
        mYoutubeLayout.setBackgroundColor(getResources().getColor(R.color.C000000));
        mContext.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mContext.getWindow().getDecorView().invalidate();
        FrameLayout.LayoutParams lp0 = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        lp0.gravity = Gravity.CENTER;
        mSimpleExoPlayerView.setLayoutParams(lp0);
    }

    public void initViewPortrait() {
        if (Build.VERSION.SDK_INT >= 14)
            toggleHideyBar(false);
        mYoutubeLayout.setBackgroundColor(getResources().getColor(R.color.C000000));
        WindowManager.LayoutParams attrs = ((Activity) mContext).getWindow().getAttributes();
        attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mContext.getWindow().setAttributes(attrs);
        mContext.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        float width = UIUtils.getScreenWidth();
        float height = width * VIDEO_HBW;
        mSimpleExoPlayerView.getLayoutParams().height = (int) height;
        mSimpleExoPlayerView.getLayoutParams().width = (int) width;
    }

    public void toggleHideyBar(boolean isLand) {
        try {
            int uiOptions = mContext.getWindow().getDecorView().getSystemUiVisibility();
            int newUiOptions = uiOptions;
            boolean isImmersiveModeEnabled = ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);
            if (isImmersiveModeEnabled) {
                if (isLand)
                    return;
            } else {
                if (!isLand)
                    return;
            }
            if (Build.VERSION.SDK_INT >= 14) {
                newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            }
            if (Build.VERSION.SDK_INT >= 16) {
                newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
            }
            if (Build.VERSION.SDK_INT >= 18) {
                newUiOptions ^= View.STATUS_BAR_HIDDEN;
                newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            }
            mContext.getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
        } catch (Exception ex) {
        }
    }

    class CtrlScreenListener implements ExoPlayerCtrlScreenListener {

        @Override
        public void onExpandScreen() {
             mContext.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        @Override
        public void onShrinkScreen() {
             mContext.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    public boolean removeVideoFragment() {
        mContext.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (mContext instanceof MainActivity) {
            ((MainActivity) mContext).removeVideoFragment();
            return true;
        }
        return false;
    }

    public boolean isMiniVideo() {
        return isMiniVideo;
    }

    public void minVideoView() {
        if (null != mYoutubeLayout && mYoutubeLayout.isMax()) {
            isMiniVideo = true;
            mYoutubeLayout.minimize();
        }
    }

    @Override
    public void beenMaxCallback() {
        mSimpleExoPlayerView.setUseController(true);
        mLinearlayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void startChangeCallback() {
        Log.e("==aaaa===", "=======");
        mSimpleExoPlayerView.setUseController(false);
        if (isMiniVideo()) {
            mLinearlayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void closeViewCallback() {
        removeVideoFragment();
    }

    private final static int MSG_MIX_VIDEO = 0;
    private final static int MSG_LANDSCAPE = MSG_MIX_VIDEO + 1;
    private final static int MSG_PORTRAIT = MSG_LANDSCAPE + 1;

    private MyHandler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private WeakReference<CandyVideoView> mCandyVideoView = null;

        public MyHandler(CandyVideoView candyVideoView) {
            mCandyVideoView = new WeakReference<CandyVideoView>(candyVideoView);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            CandyVideoView candyVideoView = mCandyVideoView.get();
            if (null != candyVideoView) {
                switch (msg.what) {
                    case MSG_MIX_VIDEO:
                        candyVideoView.minVideoView();
                        break;
                    case MSG_PORTRAIT:
                        candyVideoView.initViewPortrait();
                        break;
                    case MSG_LANDSCAPE:
                        candyVideoView.initViewLandscape();
                        break;
                }

            }
        }
    }
}
