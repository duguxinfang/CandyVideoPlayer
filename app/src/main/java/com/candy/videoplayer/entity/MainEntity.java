package com.candy.videoplayer.entity;

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

public class MainEntity {
    private String thumbUrl;
    private String videoUrl;

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}
