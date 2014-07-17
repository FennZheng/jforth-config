package org.xforth.config.manager;

public interface IRemoteConfigManager {
    public void importProperties(String schema,String fileName);
    public void exportProperties();
}
