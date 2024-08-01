package com.cjx.aiquestion.utils;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;

/**
 * 测试Redis
 *
 * @author cjx
 * @date 2024-07-31
 */
@SpringBootTest
public class RedisTest {

    @Resource
    private RedisTemplate<String,String> redisTemplate;


    @Test
    public void testRedis() {
        redisTemplate.opsForValue().set("name","cjx");
        System.out.println(redisTemplate.opsForValue().get("name"));

    }

}
