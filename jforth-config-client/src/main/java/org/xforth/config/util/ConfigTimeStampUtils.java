package org.xforth.config.util;

import java.util.concurrent.ConcurrentMap;

/**
 * 毫秒级别的配置文件时间戳工具类
 */
public class ConfigTimeStampUtils {
    public static void updateTimestamp(final ConcurrentMap<String,Long> map,String watchConfig){
        map.put(watchConfig,System.currentTimeMillis());
    }
    public static void updateTimestamp(final ConcurrentMap<String,Long> map,String[] watchConfigs){
        for(String watchConfig:watchConfigs) {
            map.put(watchConfig, System.currentTimeMillis());
        }
    }
}
