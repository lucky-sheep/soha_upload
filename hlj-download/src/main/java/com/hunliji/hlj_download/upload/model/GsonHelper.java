package com.hunliji.hlj_download.upload.model;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Map;

/**
 * GsonHelper
 *
 * @author wm
 * @date 19-11-25
 */
class GsonHelper {
    private static Gson gson;

    static {
        gson = new Gson();
    }

    private GsonHelper() {
    }

    static <T> T jsonElementToBean(JsonElement jsonStr, Class<?> cl) {
        return (T) gson.fromJson(jsonStr, cl);
    }
}
