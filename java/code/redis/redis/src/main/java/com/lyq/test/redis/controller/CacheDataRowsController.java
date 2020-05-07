package com.lyq.test.redis.controller;

import com.lyq.test.redis.thread.CacheRowsThread;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

import java.util.Date;
@RestController
@RequestMapping("/redis/cacheData")
public class CacheDataRowsController {
    /**
     * 设置缓存行的调度规则
     * @param rowId
     * @param delay
     */
    @GetMapping("/setScheduleRule")
    public void scheduleRowCache(@RequestParam(value = "rowId") String rowId,
                                 @RequestParam(value = "delay") int delay) {
        Jedis conn = new Jedis("localhost");
        conn.zadd("delay:", delay, rowId);
        conn.zadd("schedule:",System.currentTimeMillis()/1000 , rowId);
    }

    /**
     * 执行缓存数据行的调度任务
     */
    @GetMapping("/cacheRows")
    public void cacheRows() {
        CacheRowsThread cacheRowsThread = new CacheRowsThread();
        cacheRowsThread.start();
        cacheRowsThread.setQuit();
    }
}
