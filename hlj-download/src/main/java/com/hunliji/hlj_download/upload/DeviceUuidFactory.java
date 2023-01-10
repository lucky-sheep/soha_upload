package com.hunliji.hlj_download.upload;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import androidx.core.app.ActivityCompat;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

final class DeviceUuidFactory {
    private static final String PREFS_FILE = "device_id.xml";
    private static final String PREFS_DEVICE_ID = "device_id";
    private static final String PREFS_IMEI = "IMEI";
    private static final String PREFS_ANDROID_ID = "ANDROID_ID";
    private static UUID uuid;
    private static DeviceUuidFactory INSTANCE;
    private static String imeiStr;
    private static String androidId;


    private DeviceUuidFactory() {
    }

    protected static DeviceUuidFactory getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DeviceUuidFactory();
        }
        return INSTANCE;
    }

    protected String getDeviceUuidString(Context context) {
        if(context==null){
            return getDeviceUuidString();
        }
        if (uuid == null) {
            synchronized (DeviceUuidFactory.class) {
                if (uuid == null) {
                    SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE, 0);
                    String id = prefs.getString(PREFS_DEVICE_ID, null);
                    if (!TextUtils.isEmpty(id)) {
                        uuid = UUID.fromString(id);
                    } else {
                        String androidId = getAndroidId(context);
                        try {
                            if (!TextUtils.isEmpty(androidId) && !"9774d56d682e549c".equals(
                                    androidId)) {
                                uuid = UUID.nameUUIDFromBytes(androidId.getBytes("utf8"));
                            } else {
                                String deviceId = null;
                                try {
                                    deviceId = getIMEI(context);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                uuid = !TextUtils.isEmpty(deviceId) ? UUID.nameUUIDFromBytes(
                                        deviceId.getBytes("utf8")) : UUID.randomUUID();
                            }
                            prefs.edit()
                                    .putString(PREFS_DEVICE_ID, uuid.toString())
                                    .apply();
                        } catch (UnsupportedEncodingException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
        return getDeviceUuidString();
    }

    private String getDeviceUuidString() {
        if (uuid != null) {
            return uuid.toString();
        }
        return "";
    }

    @SuppressLint("HardwareIds")
    private static String getIMEI(Context context) {
        if (!TextUtils.isEmpty(imeiStr)) {
            return imeiStr;
        }
        final SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE, 0);
        imeiStr = prefs.getString(PREFS_IMEI, null);
        if (TextUtils.isEmpty(imeiStr)) {
            try {
                if (ActivityCompat.checkSelfPermission(context,
                        Manifest.permission.READ_PHONE_STATE) != PackageManager
                        .PERMISSION_GRANTED) {
                    return null;
                }
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(
                        Context.TELEPHONY_SERVICE);
                if(telephonyManager!=null) {
                    imeiStr = telephonyManager.getDeviceId();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!TextUtils.isEmpty(imeiStr)) {
                prefs.edit()
                        .putString(PREFS_IMEI, imeiStr)
                        .apply();
            }
        }
        return imeiStr;
    }

    private static String getAndroidId(Context context) {
        if (!TextUtils.isEmpty(androidId)) {
            return androidId;
        }
        final SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE, 0);
        androidId = prefs.getString(PREFS_ANDROID_ID, null);
        if (TextUtils.isEmpty(androidId)) {
            try {
                androidId = Settings.System.getString(context.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!TextUtils.isEmpty(androidId)) {
                prefs.edit()
                        .putString(PREFS_ANDROID_ID, androidId)
                        .apply();
            }
        }
        return androidId;
    }
}