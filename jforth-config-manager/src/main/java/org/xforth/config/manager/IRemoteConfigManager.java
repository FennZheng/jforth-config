package org.xforth.config.manager;

public interface IRemoteConfigManager {
    public void initByProperties(String schema,String fileName) throws Exception;
    public void update(String schema,String key,String value) throws Exception;
    public void delete(String schema,String key,String value) throws Exception;
    public void exportProperties(String schema) throws Exception;
}
