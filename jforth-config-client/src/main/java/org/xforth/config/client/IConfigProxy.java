package org.xforth.config.client;

import java.util.Properties;

public interface IConfigProxy {
    public String get(String key);
    public Properties loadAll();
}