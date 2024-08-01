package com.cjx.aiquestion.utils;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

/**
 * 类的描述信息
 *
 * @author cjx
 * @date 2024-07-31
 */
@SpringBootTest
public class CaffeineTest {

    @Test
    public void test() {
        Cache<String, Object> cache = Caffeine.newBuilder()
                .initialCapacity(10)
                .maximumSize(15)
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .expireAfterAccess(1, TimeUnit.MINUTES)
                .removalListener((key, val, removalCause) -> {
                    System.out.println("key:" + key + "  val:" + val + "  removalCause:" + removalCause);
                })
                .recordStats().
                build();
        cache.put("name", "cjx");
        System.out.println(cache.getIfPresent("name"));
        cache.invalidate("name");
        System.out.println(cache.getIfPresent("name"));
    }

}
