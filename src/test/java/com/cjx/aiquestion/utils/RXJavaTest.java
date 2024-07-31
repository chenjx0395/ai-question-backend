package com.cjx.aiquestion.utils;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

/**
 * RXJava 的基本使用测试
 *
 * @author cjx
 * @date 2024-07-31
 */
@SpringBootTest
public class RXJavaTest {

    @Test
    public void test() throws InterruptedException {
        Flowable<Long> flowable = Flowable.interval(1, TimeUnit.SECONDS)
                .map(i -> i + 1)
                .subscribeOn(Schedulers.io());

        flowable.observeOn(Schedulers.io())
                .doOnNext(item -> System.out.println(item.toString()))
                .subscribe();

        Thread.sleep(10000L);
    }

}
