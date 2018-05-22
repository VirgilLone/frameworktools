package com.haoyunhu.tools.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * Created by weijun.hu on 2015/11/30.
 */
public class RedisService {

    private Logger logger = LoggerFactory.getLogger(RedisService.class);

    //默认1天
    private final static Long DEFAULT_EXPIRE_SECONDS = 86400L;

    private RedisTemplate<String, Object> redisTemplate;

    public RedisTemplate<String, Object> getRedisTemplate() {
        return redisTemplate;
    }

    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Boolean set(final String key, final Object value) {
        return set(key, value, DEFAULT_EXPIRE_SECONDS);
    }

    public Boolean set(final String key, final Object value, final Long seconds) {
        if (redisTemplate == null) {
            return false;
        }

        try {
            ValueOperations<String, Object> stringObjectValueOperations = redisTemplate.opsForValue();
            stringObjectValueOperations.set(key, value, seconds, TimeUnit.SECONDS);
        }catch (Exception e){
            logger.error("redis service set error " + e.getMessage(), e);
            return false;
        }

        return true;
    }

    public Object get(final String key) {
        if (redisTemplate == null) {
            return null;
        }

        try {
            ValueOperations<String, Object> stringObjectValueOperations = redisTemplate.opsForValue();
            return stringObjectValueOperations.get(key);
        }catch (Exception e){
            logger.error("redis service get error " + e.getMessage(), e);
            return null;
        }
    }

    public Boolean delete(final String key) {
        try {
            redisTemplate.delete(key);
        }catch (Exception e){
            logger.error("redis service delete error " + e.getMessage(), e);
            return false;
        }
        return true;
    }

    public Boolean delete(final Collection<String> keys) {
        try {
            redisTemplate.delete(keys);
        }catch (Exception e){
            logger.error("redis service deletes error " + e.getMessage(), e);
            return false;
        }
        return true;
    }

    public Long increment(final String key, final Long delta) {
        if (redisTemplate == null) {
            return 0L;
        }

        try {
            ValueOperations<String, Object> stringObjectValueOperations = redisTemplate.opsForValue();
            return stringObjectValueOperations.increment(key, delta);
        }catch (Exception e){
            logger.error("redis service increment error " + e.getMessage(), e);
            return 0L;
        }
    }

    public Boolean expire(final String key, final long timeout) {
        return expire(key, timeout, TimeUnit.SECONDS);
    }

    public Boolean expire(final String key, final long timeout, final TimeUnit unit) {
        if (redisTemplate == null) {
            return false;
        }

        try {
            return redisTemplate.expire(key, timeout, unit);
        }catch (Exception e){
            logger.error("redis service expire error " + e.getMessage(), e);
            return false;
        }
    }

    public Long getExpire(final String key) {
        if (redisTemplate == null) {
            return 0L;
        }

        try {
            return redisTemplate.getExpire(key);
        }catch (Exception e){
            logger.error("redis service getExpire error " + e.getMessage(), e);
            return 0L;
        }
    }

    /**
     * 获取 RedisSerializer
     */
    protected RedisSerializer<String> getRedisSerializer() {
        return redisTemplate.getStringSerializer();
    }

}
