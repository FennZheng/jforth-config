package org.xforth.config.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.util.CollectionUtils;
import org.xforth.config.client.ConfigBundle;
import java.io.IOException;
import java.util.Properties;

public class DistributedPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {
    private static final Logger logger = LoggerFactory.getLogger(DistributedPropertyPlaceholderConfigurer.class);
    private ConfigBundle configBundle;
    /**
     * Return a merged Properties instance containing both the
     * loaded properties and properties set on this FactoryBean.
     */
    @Override
    protected Properties mergeProperties() throws IOException {
        Properties result = new Properties();

        if (this.localOverride) {
            // Load properties from file upfront, to let local properties override.
            loadProperties(result);
        }

        if (this.localProperties != null) {
            for (Properties localProp : this.localProperties) {
                CollectionUtils.mergePropertiesIntoMap(localProp, result);
            }
        }

        if (!this.localOverride) {
            // Load properties from file afterwards, to let those properties override.
            loadProperties(result);
        }
        loadCentralProperties(result);
        return result;
    }

    /**
     * load central proxy
     */
    protected void loadCentralProperties(Properties properties){
        properties.putAll(configBundle.loadAll());
    }

    public ConfigBundle getConfigBundle() {
        return configBundle;
    }

    public void setConfigBundle(ConfigBundle configBundle) {
        this.configBundle = configBundle;
    }
}
