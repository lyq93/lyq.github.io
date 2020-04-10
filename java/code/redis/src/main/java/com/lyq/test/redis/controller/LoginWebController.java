package com.lyq.test.redis.controller;

import com.lyq.test.redis.thread.CleanSessionThread;
import com.lyq.test.redis.utils.RequestUtil;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

import java.util.Set;

@RestController
@RequestMapping("/redis/login")
public class LoginWebController {
    /**
     * 检查token是否有效
     * @param token
     * @return
     */
    @GetMapping("/checkLogin")
    public String checkLogin(@RequestParam(value = "token") String token) {
        Jedis conn = new Jedis("localHost");
        return conn.hget("login:", token);
    }

    /**
     * 更新token，同时记录最近浏览过的商品
     * @param token
     * @param user
     * @param item
     */
    @GetMapping("/updateToken")
    public void updateToken(@RequestParam(value = "token") String token,
                            @RequestParam(value = "user") String user,
                            @RequestParam(value = "item") String item) {
        Jedis conn = new Jedis("localhost");
        conn.hset("login:", token, user);//重新添加token与用户的关联
        Long nowTime = System.currentTimeMillis() / 1000;
        conn.zadd("recent:", nowTime, token);//记录token的最后一次出现的时间

        if(!StringUtils.isEmpty(item)) {
            conn.zadd("viewed:" + token, nowTime, item);
            conn.zremrangeByRank("viewed:" + token, 0, -26);
            conn.zincrby("viewed:", -1 , item);
        }
    }

    /**
     * 清除session
     */
    @GetMapping("/cleanSession")
    public void cleanSession() {
        CleanSessionThread cleanSessionThread = new CleanSessionThread(1000);
        cleanSessionThread.start();//启动线程
        cleanSessionThread.quit();//另线程退出清除session
    }

    /**
     * 添加购物车
     * @param sessionId
     * @param item
     * @param count
     */
    @GetMapping("/addCart")
    public void addCart(@RequestParam(value = "sessionId") String sessionId,
                        @RequestParam(value = "item") String item,
                        @RequestParam(value = "count") int count) {
        Jedis conn = new Jedis("localhost");
        if(count > 0) {
            conn.hset("cart:" + sessionId, item, String.valueOf(count));
        } else {
            conn.hdel("cart:" + sessionId, item);
        }
    }

    /**
     * 缓存请求返回的内容
     * @param request
     * @param callback
     * @return
     */
    @GetMapping("/cacheRequest")
    public String cacheRequest(@RequestParam(value = "request") String request,
                               @RequestParam(value = "callback") Callback callback) {
        Jedis conn = new Jedis("localhost");
        String content = null;
        if(conn.exists(request)) {
            content = conn.get(request);
        } else {
            String pageKey = RequestUtil.HashRequest(request);//获取当次请求的哈希值作为缓存的key
            content = callback.call(request);
            conn.setex(pageKey, 300, content);
        }
        return content;
    }

    public interface Callback {
        public String call(String request);
    }
}
