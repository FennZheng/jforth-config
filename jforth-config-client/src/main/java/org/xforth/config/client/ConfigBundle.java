package org.xforth.config.client;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * 管理LocalConfig和RemoteConfig
 */
@Component
public class ConfigBundle implements IConfigProxy {
    private static final Logger logger = LoggerFactory.getLogger(ConfigBundle.class);
    private static LocalConfig localConfig;
    private static RemoteConfig remoteConfig;

    public ConfigBundle(String schema,String configFile,String zkConnectString,boolean isDynamic){
        localConfig = new LocalConfig(configFile,isDynamic);
        remoteConfig = new RemoteConfig(schema,zkConnectString,isDynamic);
    }
    /**
     * 提供配置读取
     * @param key
     * @return
     */
    @Override
    public String get(String key){
        if(StringUtils.isNotBlank(key)){
            String localVal = localConfig.get(key);
            if(StringUtils.isNotBlank(localVal)){
                if(logger.isDebugEnabled()){
                    //logger.debug("ConfigBundle get config from local key:{} value:{}",key,localVal);
                }
                return localVal;
            }else{
                String remoteVal = remoteConfig.get(key);
                if(logger.isDebugEnabled()){
                    //logger.debug("ConfigBundle get config from remote key:{} value:{}",key,remoteVal);
                }
                return remoteVal;
            }
        }
        if(logger.isDebugEnabled()){
            logger.debug("ConfigBundle can not find key:{}",key);
        }
        return null;
    }
    @Override
    public Properties loadAll(){
        Properties localProp = localConfig.loadAll();
        Properties remoteProp = remoteConfig.loadAll();
        Properties merged = new Properties();
        merged.putAll(remoteProp);
        merged.putAll(localProp);
        return merged;
    }
    //test
    public static String val(String key){
        if(StringUtils.isNotBlank(key)){
            String localVal = localConfig.get(key);
            if(StringUtils.isNotBlank(localVal)){
                if(logger.isDebugEnabled()){
                    //logger.debug("ConfigBundle get config from local key:{} value:{}",key,localVal);
                }
                return localVal;
            }else{
                String remoteVal = remoteConfig.get(key);
                if(logger.isDebugEnabled()){
                    //logger.debug("ConfigBundle get config from remote key:{} value:{}",key,remoteVal);
                }
                return remoteVal;
            }
        }
        if(logger.isDebugEnabled()){
            logger.debug("ConfigBundle can not find key:{}",key);
        }
        return null;
    }
}
