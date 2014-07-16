package org.xforth.config.client;

import org.apache.commons.lang3.StringUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 单例 增量更新
 */
public class RemoteConfig implements IDynamicConfig {
    private static final Logger logger = LoggerFactory.getLogger(RemoteConfig.class);
    private static ConcurrentHashMap<String,String> remoteConfigMap = new ConcurrentHashMap<String, String>();
    private static CuratorFramework zkClient;
    private static final String DEFAULT_ENCODE = "utf-8";
    private static final String DEFAULT_NAMESPACE = "/config-center";
    private static volatile String servicePath;
    private String zkConnectString;

    private String appName;
    public RemoteConfig(String appName,String zkConnectString){
        this.appName = appName;
        this.zkConnectString = zkConnectString;
        generateServicePath();
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        zkClient = CuratorFrameworkFactory.newClient(zkConnectString, retryPolicy);
        zkClient.start();
        //registerWatcher();
        loadConfig(null);
    }
    @Override
    public String get(String key) {
        return remoteConfigMap.get(key);
    }

    @Override
    public void loadConfig(Object data) {
        try {
            if(zkClient.checkExists().forPath(servicePath)!=null) {
                List<String> childPaths = zkClient.getChildren().forPath(servicePath);
                for(String path:childPaths){
                    byte[] valBytes = zkClient.getData().forPath(servicePath+"/"+path);
                    if(valBytes!=null) {
                        String val = new String(valBytes, DEFAULT_ENCODE);
                        remoteConfigMap.put(path.trim(), val.trim());
                    }
                }
/*
                byte[] bytes = zkClient.getData().forPath(buildPath());
                //保护&&是否需要开关
                if(bytes==null||bytes.length<=0){
                    return;
                }
                String jsonConfig = new String(bytes,DEFAULT_ENCODE);
                if (StringUtils.isNotBlank(jsonConfig)) {
                    Map configMap = JSON.parseObject(jsonConfig, Map.class);
                    Iterator iterator = configMap.keySet().iterator();
                    while (iterator.hasNext()) {
                        Object key = iterator.next();
                        remoteConfigMap.put(key.toString(), configMap.get(key).toString());
                    }
                }
*/
            }
        } catch (Exception e) {
            logger.error("localConfig init error:{}",e);
        }
    }

    @Override
    public void registerWatcher() {
        final PathChildrenCache pathChildrenCache = new PathChildrenCache(zkClient, servicePath, true);
        pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                String path = event.getData().getPath();
                String key = path.replaceAll(servicePath + "/", "").trim();
                String val = event.getData().toString().trim();
                switch (event.getType()) {
                    case CHILD_ADDED:
                        remoteConfigMap.put(key,val);
                        break;
                    case CHILD_REMOVED:
                        remoteConfigMap.remove(key);
                        break;
                    case CHILD_UPDATED:
                        remoteConfigMap.put(key,val);
                        break;
                }
            }
        });
        try {
            pathChildrenCache.start();
        } catch (Exception e) {
            logger.error("registerWatcher zookeeper path error", e);
        }
        /*NodeCache nodeCache = new NodeCache(zkClient,buildPath(),true);
        nodeCache.getListenable().addListener(new NodeCacheListener() {
            @Override
            public void nodeChanged() throws Exception {
                loadConfig(null);
            }
        });*/
    }

    /**
     * build path
     */
    private String generateServicePath(){
        if(servicePath==null) {
            checkNotNull(appName, "appName can't be null");
            servicePath =  DEFAULT_NAMESPACE.concat("/").concat(appName);
        }
        return servicePath;
    }
    private void checkNotNull(String str,String errorLogMsg){
        if(StringUtils.isBlank(str)) {
            logger.error(errorLogMsg);
            throw new NullPointerException(errorLogMsg);
        }
    }
}
