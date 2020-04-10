package com.lyq.test.redis.thread;

import redis.clients.jedis.Jedis;

import java.util.Set;

public class CleanSessionThread extends Thread {

    private int limit;//token最大保留数量
    private Jedis conn;//redis连接对象
    private Boolean quit;//是否离开循环的状态

    public CleanSessionThread(int limit) {
        this.limit = limit;
        this.conn = new Jedis("localhost");
        this.quit = false;
    }

    /**
     * 改变是否跳出循环的状态
     */
    public void quit() {
        this.quit = true;
    }

    /**
     * 守护进程：保留指定数据的token信息，防止占用内存过大
     */
    @Override
    public void run() {
        while (!quit) {
            Long tokenSize = conn.zcard("recent:");//获取集合的数据大小
            if(tokenSize > limit) {
                long endIndex = Math.min(tokenSize - limit, 100);
                Set<String> tokens = conn.zrange("recent:", 0, endIndex);//获取给定区间的成员
                for(String token : tokens) {
                    conn.hdel("login:", token);
                    conn.zrem("recent:",token);
                    conn.del("viewed:" + token);
                    conn.hdel("cart:" + token);
                }
            } else {
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }
        }
    }
}
