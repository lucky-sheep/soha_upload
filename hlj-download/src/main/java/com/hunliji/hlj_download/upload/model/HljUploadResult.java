package com.hunliji.hlj_download.upload.model;

import android.text.TextUtils;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Suncloud on 2016/8/24.
 */
public class HljUploadResult {
    private String domain;
    @SerializedName(value = "image_path", alternate = {"video_path", "audio_path"})
    String path;
    @SerializedName("persistent_id")
    private String persistentId;
    private int width;
    private int height;
    private String hash;
    private String key;
    private JsonElement orientation;

    @SerializedName("avinfo")
    private JsonElement avinfoJson;
    private QiNiuAvInfo qiNiuAvInfo;

    private String url;

    public String getUrl() {
        if (!TextUtils.isEmpty(url)) {
            return url;
        }
//        if (TextUtils.isEmpty(domain) || TextUtils.isEmpty(path)) {
//            return "";
//        }
        return Domain.INSTANCE.getDoamin() +""+ key;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public String getPersistentId() {
        return persistentId;
    }

    public int getWidth() {
        if (isRotate()) {
            return height;
        }
        return width;
    }

    public int getHeight() {
        if (isRotate()) {
            return width;
        }
        return height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    private boolean isRotate() {
        if (orientation == null) {
            return false;
        }
        try {
            String val = orientation.getAsJsonObject()
                    .get("val")
                    .getAsString();
            switch (val.toLowerCase()) {
                case "left-top":
                case "left-bottom":
                case "right-top":
                case "right-bottom":
                    return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public QiNiuAvInfo getAvinfo() {
        if (qiNiuAvInfo != null) {
            return qiNiuAvInfo;
        }
        if (avinfoJson == null) {
            return null;
        }
        try {
            qiNiuAvInfo = GsonHelper.jsonElementToBean(avinfoJson, QiNiuAvInfo.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return qiNiuAvInfo;
    }

    public int getVideoWidth() {
        try {
            return getAvinfo().getVideoInfo()
                    .getWidth();
        } catch (Exception ignored) {

        }
        return 0;
    }

    public int getVideoHeight() {
        try {
            return getAvinfo().getVideoInfo()
                    .getHeight();
        } catch (Exception ignored) {

        }
        return 0;
    }

    public float getVideoDuration() {
        try {
            return getAvinfo().getVideoInfo()
                    .getDuration();
        } catch (Exception ignored) {

        }
        return 0;
    }

    public String getHash() {
        return hash;
    }
}
