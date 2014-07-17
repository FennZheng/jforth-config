package org.xforth.config.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.util.ObjectUtils;
import org.xforth.config.client.ConfigBundle;

import java.util.Enumeration;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

public class DistributedPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {
    private static final Logger logger = LoggerFactory.getLogger(DistributedPropertyPlaceholderConfigurer.class);
    private static volatile AtomicBoolean inited = new AtomicBoolean(false);
    private ConfigBundle configBundle;
    /**
     * 重写父类方法，代理加载jforth-config
     */
    @Override
    protected void convertProperties(Properties props) {
        if(inited.compareAndSet(false,true)){
            //加载jforth config
            props.putAll(configBundle.loadAll());
        }
        Enumeration<?> propertyNames = props.propertyNames();
        while (propertyNames.hasMoreElements()) {
            String propertyName = (String) propertyNames.nextElement();
            String propertyValue = props.getProperty(propertyName);
            String convertedValue = convertProperty(propertyName, propertyValue);
            if (!ObjectUtils.nullSafeEquals(propertyValue, convertedValue)) {
                props.setProperty(propertyName, convertedValue);
            }
        }
    }

    public ConfigBundle getConfigBundle() {
        return configBundle;
    }

    public void setConfigBundle(ConfigBundle configBundle) {
        this.configBundle = configBundle;
    }
}
