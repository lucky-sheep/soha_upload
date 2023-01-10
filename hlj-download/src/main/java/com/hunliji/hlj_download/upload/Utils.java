package com.hunliji.hlj_download.upload;

import java.security.MessageDigest;

/**
 * @author kou_zhong
 * @date 2020/10/20
 */
class Utils {
    public static boolean isBlank(String str) {
        int length;
        if (str != null && (length = str.length()) != 0) {
            for (int i = 0; i < length; ++i) {
                if (!Character.isWhitespace(str.charAt(i))) {
                    return false;
                }
            }
        }
        return true;
    }

    public static String MD5(String str) {
        try {
            if (isBlank(str)) {
                return null;
            } else {
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(str.getBytes("UTF-8"));
                byte[] tmp = md.digest();
                StringBuilder sb = new StringBuilder();

                for (int i = 0; i < 16; ++i) {
                    sb.append(String.format("%02x", tmp[i]));
                }

                return sb.toString();
            }
        } catch (Exception var5) {
            return null;
        }
    }
}
