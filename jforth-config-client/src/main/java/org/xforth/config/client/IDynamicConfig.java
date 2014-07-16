package org.xforth.config.client;

public interface IDynamicConfig extends IConfigProxy{
    public void loadConfig(Object param);
    public void registerWatcher();
}
