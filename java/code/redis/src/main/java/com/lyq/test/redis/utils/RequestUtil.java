package com.lyq.test.redis.utils;

public class RequestUtil {
    /**
     * 返回请求的哈希值
     * @param request
     * @return
     */
    public static String HashRequest(String request) {
        return String.valueOf(request.hashCode());
    }
}
