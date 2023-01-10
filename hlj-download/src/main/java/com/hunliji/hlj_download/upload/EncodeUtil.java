package com.hunliji.hlj_download.upload;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

final class EncodeUtil {
	private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        'A', 'B', 'C', 'D', 'E', 'F' };

	private static String toHexString(byte[] b) {
		 StringBuilder sb = new StringBuilder(b.length * 2);
		for (byte aB : b) {
			sb.append(HEX_DIGITS[(aB & 0xf0) >>> 4]);
			sb.append(HEX_DIGITS[aB & 0x0f]);
		}
		 return sb.toString();  
	}

	protected static String md5sum(File file) {
		InputStream fis;
		byte[] buffer = new byte[1024];
		int numRead = 0;
		MessageDigest md5;
		try{
			fis = new FileInputStream(file);
			md5 = MessageDigest.getInstance("MD5");
			while((numRead=fis.read(buffer)) > 0) {
				md5.update(buffer,0,numRead);
			}
			fis.close();
			return toHexString(md5.digest());
		} catch (Exception e) {
			System.out.println("error");
			return null;
		}
	}
}

