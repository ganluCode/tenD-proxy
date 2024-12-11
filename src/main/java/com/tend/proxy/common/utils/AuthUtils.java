package com.tend.proxy.common.utils;

import com.quantum.auth.Authcode;
import com.quantum.auth.RSA2048;
import com.quantum.auth.RandomTools;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.Key;
import java.util.HashMap;
import java.util.Map;

/**
 * 加密解密工具类
 */
public class AuthUtils {
    static RSA2048 rsa2048;
    static Authcode authcode;
    public static final String CRYPT_KEY = "key";
    public static final String CRYPT_VALUE = "value";

    public static RSA2048 getRsa2048() {
        if (rsa2048 == null) {
            synchronized (RSA2048.class) {
                if (rsa2048 == null) {
                    rsa2048 = new RSA2048();
                }
            }
        }
        return rsa2048;
    }

    public static Authcode getAuthCode() {
        if (authcode == null) {
            synchronized (Authcode.class) {
                if (authcode == null) {
                    authcode = new Authcode();
                }
            }
        }
        return authcode;
    }

    public static Map<String, Object> encode(String source, Key key) throws Exception {
        Map result = new HashMap<String, Object>();
        // 随机字符串
        String random = RandomTools.generateRandom();
        try {
            source = URLEncoder.encode(source, "UTF-8");
        } catch (UnsupportedEncodingException e) {
        }
        // DES加密
        String value = getAuthCode().encode(source, random);
        if (value == null) {
        }
        result.put(CRYPT_VALUE, getAuthCode().encode(source, random));


        // RSA加密
        String encrypt = null;
        try {
            encrypt = getRsa2048().encode(random, key);
        } catch (Exception e) {
        }
        if (encrypt == null) {
        }
        result.put(CRYPT_KEY, encrypt);
        return result;
    }

    public static String decode(Map<String, Object> result, Key key) throws Exception {
        String _key = result.get(CRYPT_KEY).toString();
        String _value = result.get(CRYPT_VALUE).toString();
        try {
            _key = getRsa2048().decode(_key, key);
        } catch (Exception ex) {
        }
        if (_key == null) {
            throw new Exception("服务调用失败：解密失败");
        }
        // des解密
        _value = getAuthCode().decode(_value, _key);
        if (_value == null) {
        }
        try {
            _value = URLDecoder.decode(_value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
        }
        return _value;
    }

}
