package org.xforth.config.manager;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;

public class ConfigManagerMain {
    public static void main(String args[]) throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:configBundle-manager.xml");
        RemoteConfigManager remoteConfigManager = (RemoteConfigManager) context.getBean("remoteConfigManager");
        File config = new File(remoteConfigManager.getClass().getClassLoader().getResource("mkt-appservice.properties").toURI());
        remoteConfigManager.initByProperties("mkt-appservice",config);
    }
}
