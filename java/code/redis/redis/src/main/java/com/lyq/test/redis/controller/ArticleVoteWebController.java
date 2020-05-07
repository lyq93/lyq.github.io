package com.lyq.test.redis.controller;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ZParams;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/redis/article")
public class ArticleVoteWebController {
    private final int one_weak_time = 7 * 24 * 60 * 60;

    /**
     * 文章投票
     * @param userName
     * @param article
     * @return
     */
    @GetMapping(path = "/articleVote")
    @Transactional
    public Boolean articleVote(@RequestParam(value = "userName") String userName, @RequestParam(value = "article") String article) {
        Long cutOff = (System.currentTimeMillis()/1000) - one_weak_time;
        Jedis conn = new Jedis("localhost");
        //文章发表时间是否超过一周
        if(conn.zscore("time:",article) < cutOff) {
            return false;
        }

        String articleId = article.split(":")[1];
        //用户是否是第一次投票
        if(conn.sadd("voted:" + articleId, userName) == 1) {
            conn.zincrby("score:",200, article);
            conn.hincrBy(article, "votes", 1);
            return true;
        } else {
            return false;
        }
    }

    @GetMapping("/postArticle")
    @Transactional
    public String postArticle(@RequestParam(value = "userName") String userName, @RequestParam(value = "articleName") String articleName,
                              @RequestParam(value = "title") String title, @RequestParam(value = "link") String link){
        final int initScore = 0;
        Jedis conn = new Jedis("localhost");
        String articleId = String.valueOf(conn.incr("article:"));
        String votedKey = "voted:" + articleId;
        conn.sadd(votedKey, userName);//把文章加入到已投票名单中
        conn.expire(votedKey, one_weak_time);//设置文章过期时间

        //把文章信息加到文章列表中
//        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-DD HH:mm:ss");
        Long now = System.currentTimeMillis()/1000;

        Map<String,String> articleMap = new HashMap<>();
        articleMap.put("title",title);
        articleMap.put("link", link);
        articleMap.put("author", userName);
        articleMap.put("createdTime", String.valueOf(now));
        articleMap.put("voted", "1");
        conn.hmset("article:" + articleId, articleMap);

        conn.zadd("score:", now + initScore, "article:" + articleId);
        conn.zadd("time:", now, "article:" + articleId);

        return articleId;
    }

    /**
     * 分页获取文章列表
     * @param pageNum
     * @param pageSize
     * @param order
     * @return
     */
    @GetMapping("/getArticles")
    public List<Map<String,String>> getArticles(@RequestParam(value = "pageNum") int pageNum,
                                                @RequestParam(value = "pageSize") int pageSize,
                                                @RequestParam(value = "order") String order) {
        Jedis conn = new Jedis("localhost");
        //order为排序规则（分数和时间），取出区间内的数据
        Set<String> ids = conn.zrevrange(order, (pageNum - 1) * pageSize, pageNum * pageSize);
        List<Map<String,String>> resultArticles = new ArrayList<>();
        for(String id : ids) {
            //根据id获取相应的文章info
            Map<String, String> articleData = conn.hgetAll(id);
            articleData.put("id",id);
            resultArticles.add(articleData);
        }

        return resultArticles;
    }

    /**
     * 把文章添加到对应的群组中
     * @param articleId
     * @param addGroups
     */
    @GetMapping("/addGroups")
    public void addArticleToGroup(@RequestParam(value = "articleId") String articleId,
                                  @RequestParam(value = "addGroups") String[] addGroups) {
        String articleKey = "article:" + articleId;
        Jedis conn = new Jedis("localhost");
        for(String groupName : addGroups) {
            conn.sadd("group:" + groupName, articleKey);
        }
    }

    @GetMapping("/getArticlesByScoreOrder")
    public List<Map<String,String>> getArticlesByGroup(@RequestParam(value = "group") String group,
                                   @RequestParam(value = "pageSize") int pageSize,
                                   @RequestParam(value = "pageNum") int pageNum) {
        return this.getArticlesByGroup(group, pageSize, pageNum, "score:");
    }

    /**
     * 从分组中根据给定排序规则分页获取文章列表
     * @param group
     * @param pageSize
     * @param pageNum
     * @param order
     * @return
     */
    @GetMapping("/getArticlesByGroup")
    public List<Map<String,String>> getArticlesByGroup(@RequestParam(value = "group") String group,
                                                       @RequestParam(value = "pageSize") int pageSize,
                                                       @RequestParam(value = "pageNum") int pageNum,
                                                       @RequestParam(value = "order") String order) {
        Jedis conn = new Jedis("localhost");
        String groupKey = order + group;
        ZParams zParams = new ZParams().aggregate(ZParams.Aggregate.MAX);
        conn.zinterstore(groupKey, zParams, "group:" + group, order);
        conn.expire(groupKey, 60);
        return this.getArticles(pageNum, pageSize, groupKey);
    }
}
