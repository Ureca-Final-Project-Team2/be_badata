package com.TwoSeaU.BaData.global.redis;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RedisUtil {

    private final RedisTemplate<String,String> redisTemplate;

    public void saveValueByKey(final String key,final String value,final Long time, TimeUnit timeUnit){
        redisTemplate.opsForValue().set(key,value,time, timeUnit);
    }

    public void deleteValueByKey(final String key){
        redisTemplate.delete(key);
    }

    public String getValueByKey(final String key){
        return redisTemplate.opsForValue().get(key);
    }
}

