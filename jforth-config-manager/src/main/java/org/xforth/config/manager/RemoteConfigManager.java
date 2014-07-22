package org.xforth.config.manager;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Properties;

public class RemoteConfigManager implements IRemoteConfigManager{
    private static final Logger logger = LoggerFactory.getLogger(RemoteConfigManager.class);
    private static final String DEFAULT_NAMESPACE = "/config-center";
    private static final String DEFAULT_DECODE = "utf-8";
    private CuratorFramework zkClient;
    private String zkConnectString;
    public RemoteConfigManager(){
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        zkClient = CuratorFrameworkFactory.newClient(zkConnectString, retryPolicy);
        zkClient.start();
    }

    /**
     * check schema not exists
     * @param schema
     * @param fileName
     * @throws Exception
     */
    @Override
    public void initByProperties(String schema,String fileName) throws Exception {
        String servicePath = generateServicePath(schema);
        checkServiceConfigExists(servicePath);
        Properties prop = new Properties();
        InputStream input = null;
        try {
            input = new FileInputStream(fileName);
            prop.load(input);
        } catch (IOException e) {
            logger.error("importProperties load properties exception:{}",e);
        }
        Enumeration enums = prop.keys();
        while (enums.hasMoreElements()) {
            Object key = enums.nextElement();
            prop.get(key);
            importRemoteConfig(servicePath,key.toString(),prop.get(key).toString());
        }
    }

    private void checkServiceConfigExists(String servicePath){
        try {
            if(zkClient.checkExists().forPath(servicePath)!=null){
                throw new RuntimeException("this service config have been inited for servicePath:"+servicePath);
            }
        } catch (Exception e) {
            logger.error("checkServiceConfigExists exception:{}",e);
        }
    }
    private void importRemoteConfig(String servicePath,String key,String value) throws Exception {
        final String keyPath = servicePath.concat("/").concat(key);
        if(zkClient.checkExists().forPath(keyPath)==null){
            zkClient.create().withMode(CreateMode.PERSISTENT).forPath(servicePath+"/"+key,value.getBytes(DEFAULT_DECODE));
        }else{
            throw new RuntimeException("this service config have been inited for keyPath:"+keyPath);
        }
    }
    private String generateServicePath(String schema){
        String servicePath = DEFAULT_NAMESPACE.concat("/").concat(schema);
        return servicePath;
    }

    @Override
    public void update(String schema, String key, String value) {

    }

    @Override
    public void delete(String schema, String key, String value) {

    }

    @Override
    public void exportProperties() {

    }

    public String getZkConnectString() {
        return zkConnectString;
    }
    @Value("${zookeeper.host:}")
    public void setZkConnectString(String zkConnectString) {
        this.zkConnectString = zkConnectString;
    }
}
