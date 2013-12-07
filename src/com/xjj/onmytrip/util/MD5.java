package com.xjj.onmytrip.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 提供字符串转换为MD5的方法
 * @author XJJ
 *
 */
public class MD5 {

	public static String getMD5(String val){
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");  
			md5.update(val.getBytes());  
			byte[] m = md5.digest();//加密
			
			StringBuffer sb = new StringBuffer();  
			for(int i = 0; i < m.length; i ++){  
				String h = Integer.toHexString(0xFF & m[i]);
	            while (h.length() < 2)
	                h = "0" + h;
			    sb.append(h);  
			}  
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		return "";
	}
}
