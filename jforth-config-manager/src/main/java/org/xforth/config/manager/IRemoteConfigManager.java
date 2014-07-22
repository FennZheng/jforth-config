package org.xforth.config.manager;

public interface IRemoteConfigManager {
    public void initByProperties(String schema,String fileName) throws Exception;
    public void update(String schema,String key,String value);
    public void delete(String schema,String key,String value);
    public void exportProperties();
}
