package org.xforth.config.manager;

import java.io.File;
import java.util.Map;

public interface IRemoteConfigManager {
    public void initByProperties(String schema,File file) throws Exception;
    public void update(String schema,String key,String value) throws Exception;
    public void delete(String schema,String key,String value) throws Exception;
    public Map<String,String> exportProperties(String schema) throws Exception;
}
