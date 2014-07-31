package org.xforth.config.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xforth.config.client.ConfigBundle;
@Aspect
public class DynamicPropertyAspect {
    private static final Logger logger = LoggerFactory.getLogger(DynamicPropertyAspect.class);
    private ConfigBundle configBundle;

    @Around("execution(public * get*()) && @annotation(org.xforth.config.aop.DynamicValue)")
    public Object dynamicValueSet(ProceedingJoinPoint pjp) throws Throwable {
        final MethodSignature signature = (MethodSignature) pjp.getSignature();
        DynamicValue valueAno = signature.getMethod().getAnnotation(DynamicValue.class);
        if(valueAno!=null){
            String propKey = valueAno.value();
            try {
                return configBundle.get(propKey);
            }catch (Throwable e){
                logger.error("Method name:{} dynamicValueSet error:{}",signature.getMethod().getName(),e);
            }
        }
        Object result = pjp.proceed();
        return result;
    }

    public ConfigBundle getConfigBundle() {
        return configBundle;
    }

    public void setConfigBundle(ConfigBundle configBundle) {
        this.configBundle = configBundle;
    }
}
