package com.lyq.test.redis.controller;

import redis.clients.jedis.Jedis;

import java.util.UUID;

public class CheckDataController {
    public void waitForSync() {
        Jedis mconn = new Jedis("localhost");
        Jedis sconn = new Jedis("localhost2");

        UUID uuid = UUID.randomUUID();
        String id = uuid.toString();

        long time = System.currentTimeMillis() / 1000;

        mconn.zadd("sysnc:wait", time, id);


    }
}
