package com.alejokf;

import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;

public class CacheAsync {

    private LoadingCache<String, String> cache;

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CacheAsync cacheAsync = new CacheAsync();
        cacheAsync.initCache();
        Date initialTime = new Date();

        cacheAsync.hitCache(initialTime);
        Thread.sleep(5_000);
        cacheAsync.hitCache(initialTime);
        Thread.sleep(12_000);
        cacheAsync.hitCache(initialTime);
        Thread.sleep(25_000);
        cacheAsync.hitCache(initialTime);
    }

    private void hitCache(Date initialTime) throws ExecutionException {
        System.out.println("----------- CURRENT TIME -------------");
        Date currentTime = new Date();
        System.out.println(new Date().toString());
        System.out.println((currentTime.getTime() - initialTime.getTime()) / 1000 % 60 + " seconds");
        String result = getResult("alejo");
        System.out.println(result);
    }

    private void initCache() {
        cache = CacheBuilder.newBuilder()
                .refreshAfterWrite(10, TimeUnit.SECONDS)
                .expireAfterWrite(20, TimeUnit.SECONDS)
                .recordStats()
                .build(
                        new CacheLoader<String, String>() {
                            @Override
                            public String load(final String key) {
                                System.out.println("----------- Loading cache with key " + key);
                                return getValue(key);
                            }

                            @Override
                            public ListenableFuture<String> reload(final String key, final String prevValue) {
                                ListenableFutureTask<String> task = ListenableFutureTask
                                        .create(() -> {
                                            System.out.println("----------- Reloading cache with key " + key);
                                            return getValue(key);
                                        });
                                new ForkJoinPool().execute(task);
                                return task;
                            }
                        });
    }

    private String getValue(String key) {
        return key + "marulanda";
    }

    private String getResult(String key) throws ExecutionException {
        String value = cache.get(key);
        System.out.println("----------- CACHE STATS -------------");
        System.out.println(cache.stats());
        return value;
    }
}
