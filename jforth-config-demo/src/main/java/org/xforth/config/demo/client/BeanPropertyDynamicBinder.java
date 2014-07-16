package org.xforth.config.demo.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.xforth.config.client.ConfigBundle;

@Component
public class BeanPropertyDynamicBinder {
    @Autowired
    private ConfigBundle configBundle;

    public static void main(String args[]){
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:configBundle-demo.xml");
        BeanPropertyDynamicBinder binder = (BeanPropertyDynamicBinder)context.getBean("beanPropertyDynamicBinder");

        String oldVal = binder.configBundle.get("local.config.remote.key3");
        while(true){
            String newVal = binder.configBundle.get("local.config.remote.key3");
            //during the time,modify remote data in zookeeper
            if(!newVal.equals(oldVal)){
                oldVal = newVal;
                System.out.println("dynamic remote value change:"+oldVal);
            }
        }
    }
}
