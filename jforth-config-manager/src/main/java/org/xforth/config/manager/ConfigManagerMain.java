package org.xforth.config.manager;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ConfigManagerMain {
    public static void main(String args[]){
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:configBundle-manager.xml");
        IRemoteConfigManager remoteConfigManager = (IRemoteConfigManager) context.getBean("remoteConfigManager");
        //call method
    }
}
