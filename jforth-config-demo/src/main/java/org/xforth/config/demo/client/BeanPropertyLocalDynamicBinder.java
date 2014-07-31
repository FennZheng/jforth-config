package org.xforth.config.demo.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.xforth.config.client.ConfigBundle;

@Component
public class BeanPropertyLocalDynamicBinder {
    @Autowired
    private ConfigBundle configBundle;

    public static void main(String args[]){
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:configBundle-demo.xml");
        BeanPropertyLocalDynamicBinder binder = (BeanPropertyLocalDynamicBinder)context.getBean("beanPropertyLocalDynamicBinder");

        String oldVal = binder.configBundle.get("local.config.local.key1");
        while(true){
            String newVal = binder.configBundle.get("local.config.local.key1");
            //watch log to find out 'start watch.. dir' then modify config.properties
            if(!newVal.equals(oldVal)){
                oldVal = newVal;
                System.out.println("dynamic local value change:"+oldVal);
            }
        }
    }
}
