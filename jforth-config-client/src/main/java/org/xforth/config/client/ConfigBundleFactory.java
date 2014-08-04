package org.xforth.config.client;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class ConfigBundleFactory {
    private static final Logger logger = LoggerFactory.getLogger(ConfigBundleFactory.class);
    private static final String CONFIG_PROJECT_KEY = "config.project";
    private static final String CONFIG_FILE_KEY = "config.file";
    private static final String CONFIG_FILE_VALUE = "config.properties";
    private static final String CONFIG_REMOTE_CENTER_KEY = "config.zookeeper";
    private static final String CONFIG_SUPPORT_DYNAMIC_KEY = "config.support.dynamic";
    private static ConfigBundle configBundle;

    public ConfigBundleFactory() {
        try {
            Path file = Paths.get(this.getClass().getClassLoader().getResource(CONFIG_FILE_VALUE).toURI());
            FileReader fr = null;
            fr = new FileReader(file.toFile().getPath());
            Properties properties = new Properties();
            properties.load(fr);//load()方法可通过字符流直接加载文件
            String project = properties.getProperty(CONFIG_PROJECT_KEY);
            String configRemoteHost = properties.getProperty(CONFIG_REMOTE_CENTER_KEY);
            String configSupportDynamic = properties.getProperty(CONFIG_SUPPORT_DYNAMIC_KEY);
            logger.info("xforth config configSupportDynamic load:"+configSupportDynamic);
            if(StringUtils.isBlank(configSupportDynamic))
                configSupportDynamic = "false";
            checkValueNotNull(CONFIG_PROJECT_KEY, project);
            checkValueNotNull(CONFIG_REMOTE_CENTER_KEY, configRemoteHost);
            configBundle = new ConfigBundle(project, CONFIG_FILE_VALUE, configRemoteHost, Boolean.valueOf(configSupportDynamic));
        } catch (IOException|URISyntaxException e) {
            logger.error("initConfigBundle error:{}",e);
        }
    }

    protected void checkValueNotNull(String key, String value) {
        if (StringUtils.isBlank(value)) {
            throw new RuntimeException("properties load key is null:" + key);
        }
    }

    /**
     * factory method
     * @return
     */
    public ConfigBundle getConfigBundle() {
        return configBundle;
    }
}
