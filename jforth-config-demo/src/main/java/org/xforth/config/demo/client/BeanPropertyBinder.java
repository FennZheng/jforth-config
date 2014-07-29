package org.xforth.config.demo.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.xforth.config.aop.DynamicValue;

@Component
public class BeanPropertyBinder {
    private String staticProperty;
    private String localPro1;
    private String localPro2;
    private String overridePro1;
    private String remotePro1;
    private String remotePro2;

    public static void main(String args[]){
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:configBundle-demo.xml");
        BeanPropertyBinder binder = (BeanPropertyBinder)context.getBean("beanPropertyBinder");
        System.out.println(binder.getStaticProperty());
        System.out.println(binder.getLocalPro1());
        System.out.println(binder.getLocalPro2());
        System.out.println(binder.getOverridePro1());
        System.out.println(binder.getRemotePro1());
        System.out.println(binder.getRemotePro2());
        while(true){
            System.out.println(binder.getLocalPro1());
        }
    }

    public String getStaticProperty() {
        return staticProperty;
    }
    @Value("${staticProperty:}")
    public void setStaticProperty(String staticProperty) {
        this.staticProperty = staticProperty;
    }
    @DynamicValue
    public String getLocalPro1() {
        return localPro1;
    }
    @Value("${local.config.local.key2:}")
    public void setLocalPro1(String localPro1) {
        this.localPro1 = localPro1;
    }
    @DynamicValue
    public String getLocalPro2() {
        return localPro2;
    }
    @Value("${local.config.local.key2:}")
    public void setLocalPro2(String localPro2) {
        this.localPro2 = localPro2;
    }

    public String getOverridePro1() {
        return overridePro1;
    }
    @Value("${local.config.override.key1:}")
    public void setOverridePro1(String overridePro1) {
        this.overridePro1 = overridePro1;
    }

    public String getRemotePro1() {
        return remotePro1;
    }
    @Value("${local.config.remote.key3:}")
    public void setRemotePro1(String remotePro1) {
        this.remotePro1 = remotePro1;
    }

    public String getRemotePro2() {
        return remotePro2;
    }
    @Value("${local.config.remote.key4:}")
    public void setRemotePro2(String remotePro2) {
        this.remotePro2 = remotePro2;
    }
}
