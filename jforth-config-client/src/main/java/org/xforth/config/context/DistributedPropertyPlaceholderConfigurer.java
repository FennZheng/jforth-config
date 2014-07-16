package org.xforth.config.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.xforth.config.client.ConfigBundle;

public class DistributedPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {
    private static final Logger logger = LoggerFactory.getLogger(DistributedPropertyPlaceholderConfigurer.class);
    private ConfigBundle configBundle;
    /**
     * 重写父类方法，解密指定属性名对应的属性值
     */
    @Override
    protected String convertProperty(String propertyName,String propertyValue){
        String propVal = configBundle.get(propertyName);
        if(propVal!=null){
            logger.info("load from configBundle key:{}, value:{}",propertyName,propVal);
            return propVal;
        }
        return propertyValue;
    }

    public ConfigBundle getConfigBundle() {
        return configBundle;
    }

    public void setConfigBundle(ConfigBundle configBundle) {
        this.configBundle = configBundle;
    }
}
