package com.lyq.test.redis.thread;

import com.google.gson.Gson;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.util.Set;

public class CacheRowsThread extends Thread {
    private Jedis conn;//redis连接对象
    private Boolean quit;//离开循环标志

    public CacheRowsThread() {
        this.conn = new Jedis("localhost");
    }

    public void setQuit() {
        this.quit = true;
    }

    /**
     * 守护进程：定期执行缓存数据行
     */
    @Override
    public void run() {
        Gson gson = new Gson();
        while (!quit) {
            Set<Tuple> tuples = conn.zrangeWithScores("schedule:", 0, 0);//获取调度集合第一个任务

            Tuple tuple = tuples.size() > 0 ? tuples.iterator().next() : null;

            long now = System.currentTimeMillis() / 1000;
            if(tuple.getScore() > now) {//调度时间是否大于了当前时间
                try {
                    sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }

            String rowId = tuple.getElement();
            Double score = conn.zscore("delay:", rowId);//获取延迟时间
            if(score <= 0) {//延迟时间小于0，取消调度任务
                conn.zrem("schedule:", rowId);
                conn.zrem("delay:", rowId);
                conn.del("inv:" + rowId);
                continue;
            }

            Inventory inventory = Inventory.get(rowId);//调度任务执行后，调度集合的调度时间设置为当前时间加上延迟时间作为下次调度的时间
            conn.zadd("schedule:", now + score, rowId);
            conn.set("inv:" + rowId, gson.toJson(inventory));//缓存当前行对象
        }
    }

    public static class Inventory {
        private String id;
        private String data;
        private Long time;

        private Inventory(String id) {
            this.id = id;
            this.data = "this is data...";
            this.time = System.currentTimeMillis() / 1000;
        }

        public static Inventory get(String id) {
            return new Inventory(id);
        }
    }
}
