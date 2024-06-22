package com.chatants.config;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.date.DateUnit;
import org.springframework.ai.chat.messages.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * @description 本地缓存
 * @author Semuel Mau
 * @date 2024/2/20 14:26
 */

public class LocalCache {
    /**
     * 缓存时长
     */
    public static final long TIMEOUT = 5 * DateUnit.MINUTE.getMillis();
    /**
     * 清理间隔
     */
    private static final long CLEAN_TIMEOUT = 5 * DateUnit.MINUTE.getMillis();
    /**
     * 缓存对象
     */
    public static final TimedCache<String, Object> CACHE = CacheUtil.newTimedCache(TIMEOUT);

    /**
     * 从缓存中获取消息列表
     *
     * @param key 缓存key
     * @return 消息列表
     */
    @SuppressWarnings("unchecked")
    public static List<Message> getMessageListFromCache(String key) {
        Object cachedValue = CACHE.get(key);
        if (cachedValue instanceof List) {
            return (List<Message>) cachedValue;
        } else {
            return new ArrayList<>();
        }
    }

    static {
        //启动定时任务
        CACHE.schedulePrune(CLEAN_TIMEOUT);
    }
}
