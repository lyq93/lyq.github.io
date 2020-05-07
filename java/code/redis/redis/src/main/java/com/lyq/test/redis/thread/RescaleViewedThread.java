package com.lyq.test.redis.thread;

import redis.clients.jedis.Jedis;

public class RescaleViewedThread extends Thread {
    private Jedis conn;
    private Boolean quit;

    public RescaleViewedThread() {
        this.conn = new Jedis("localhost");
    }

    public void setQuit() {
        this.quit = true;
    }

    /**
     * 守护进程：只保留用户浏览的前2w个商品
     */
    @Override
    public void run() {
        while (!quit) {
            conn.zremrangeByRank("viewed:",0,-20001);
            try {
                sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
