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
import java.util.Enumeration;
import java.util.Properties;

public class RemoteConfigManager implements IRemoteConfigManager{
    private static final Logger logger = LoggerFactory.getLogger(RemoteConfigManager.class);
    private static final String DEFAULT_NAMESPACE = "/config-center";
    private CuratorFramework zkClient;
    private String zkConnectString;
    public RemoteConfigManager(){
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        zkClient = CuratorFrameworkFactory.newClient(zkConnectString, retryPolicy);
        zkClient.start();
    }
    @Override
    public void importProperties(String schema,String fileName) {
        String servicePath = generateServicePath(schema);
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
            updateRemoteConfig(servicePath,key.toString(),prop.get(key).toString());
        }
    }
    }

    private void updateRemoteConfig(String servicePath,String key,String value){
        zkClient.create().withMode(CreateMode.PERSISTENT).forPath(servicePath+"/local.config.remote.key3","local.config.remote.value3".getBytes("utf-8"));

    }
    private String generateServicePath(String schema){
        String servicePath = DEFAULT_NAMESPACE.concat("/").concat(schema);
        return servicePath;
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
