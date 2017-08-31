package com.alejokf;

import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class CacheSync {

    private LoadingCache<String, String> cache;

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CacheSync cacheSync = new CacheSync();
        cacheSync.initCache();
        Date initialTime = new Date();

        cacheSync.hitCache(initialTime);
        Thread.sleep(5_000);
        cacheSync.hitCache(initialTime);
        Thread.sleep(12_000);
        cacheSync.hitCache(initialTime);
        Thread.sleep(25_000);
        cacheSync.hitCache(initialTime);
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
