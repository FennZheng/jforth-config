package org.xforth.config.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 单例
 * 本地配置管理，同时监控配置配置文件变化，动态变更ConfigBundle
 */
public class LocalConfig implements IDynamicConfig{
    private static final Logger logger = LoggerFactory.getLogger(LocalConfig.class);
    private static WatchService watcher = null;
    private static final ExecutorService executorService = Executors.newFixedThreadPool(1);
    private static volatile ConcurrentHashMap<String,String> localConfigMap = new ConcurrentHashMap<String, String>();
    static {
        try {
            watcher = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            logger.error("init local config watch fail:"+e);
        }
    }
    private String configFile;

    public LocalConfig(String configFile){
        this.configFile = configFile;
        try {
            loadConfig(configFile);
            registerWatcher();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void registerWatcher() {
        try {
            Path dir = Paths.get(this.getClass().getClassLoader().getResource("").toURI());
            logger.info("start watch dir:{}",dir);
            WatchKey watchKey = dir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE,StandardWatchEventKinds.ENTRY_MODIFY,
                    StandardWatchEventKinds.ENTRY_DELETE);
        } catch (Exception e) {
            logger.error("registerWatcher error:{}",e);
            return;
        }
        Runnable checkConfigLoop = new Runnable() {
            @Override
            public void run() {
                while(true){
                    WatchKey key = null;
                    try {
                        key = watcher.take();
                    } catch (InterruptedException e) {
                        logger.error("checkConfigLoop InterruptedException:{}",e);
                    }
                    for (WatchEvent<?> event : key.pollEvents()) {
                        WatchEvent.Kind kind = event.kind();
                        if (kind == StandardWatchEventKinds.OVERFLOW) {
                            continue;
                        }
                        // 目录监视事件的上下文是文件名
                        WatchEvent<Path> evt = (WatchEvent<Path>)event;
                        Path name = evt.context();
                        if(name.toString().equals(configFile)){
                            logger.info("local config file:{} change ,load new config...",configFile);
                            try {
                                loadConfig(configFile);
                            }catch(Throwable e){
                                logger.error("checkConfigLoop loadConfig error:{}",e);
                            }
                        }else{
                            logger.debug("ignore event:it was not a config file changing as name:{}",name);
                        }
                    }
                    boolean valid = key.reset();
                    if (!valid)
                    {
                        break;	// Exit if directory is deleted
                    }
                }
            }
        };
        executorService.submit(checkConfigLoop);
    }
    @Override
    public void loadConfig(Object configFile){
        try {
            synchronized (this) {
                ConcurrentHashMap<String,String> newConfigMap = new ConcurrentHashMap<>();
                Path file = Paths.get(this.getClass().getClassLoader().getResource(configFile.toString()).toURI());
                FileReader fr = new FileReader(file.toFile().getPath());
                Properties properties = new Properties();
                properties.load(fr);//load()方法可通过字符流直接加载文件
                Enumeration enumeration = properties.propertyNames();
                while (enumeration.hasMoreElements()) {
                    String propertyName = enumeration.nextElement().toString();
                    logger.debug("localConfigMap put key:{},value:{}",propertyName, properties.getProperty(propertyName));
                    newConfigMap.put(propertyName, properties.getProperty(propertyName));
                }
                if(newConfigMap.size()>0)
                    localConfigMap = newConfigMap;
            }
        } catch (URISyntaxException|IOException e) {
            logger.error("loadLocalConfig error:{}",e);
        }

    }

    @Override
    public String get(String key) {
        return localConfigMap.get(key);
    }

    @Override
    public Properties loadAll() {
        Properties prop = new Properties();
        for (Map.Entry<String, String> entry : localConfigMap.entrySet()){
            prop.put(entry.getKey(),entry.getValue());
        }
        return prop;
    }

    @PreDestroy
    public void destroy(){
        if(!(executorService.isShutdown()||executorService.isTerminated())) {
            executorService.shutdownNow();
        }
    }
}
