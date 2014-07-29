package org.xforth.config.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.xforth.config.client.ConfigBundle;
@Aspect
public class DynamicPropertyAspect {
    private static final Logger logger = LoggerFactory.getLogger(DynamicPropertyAspect.class);
    @Autowired
    private ConfigBundle configBundle;
    @Around("execution(* *.get()) && within(@org.xforth.config.aop.DynamicValue *)") //定义环绕通知
    public Object dynamicValueSet(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.nanoTime();
        final MethodSignature signature = (MethodSignature) pjp.getSignature();
        Value valueAno = signature.getMethod().getAnnotation(Value.class);
        if(valueAno!=null){
            String propKey = valueAno.value();
            try {
                String propertyVal = configBundle.get(propKey);
                if(logger.isDebugEnabled()){
                    logger.debug("dynamicValueSet elapse time :"+(System.nanoTime()-start));
                }
                return propertyVal;
            }catch (Throwable e){
                logger.error("Method name:{} dynamicValueSet error:{}",signature.getMethod().getName(),e);
            }
        }
        Object result = pjp.proceed();
        return result;
    }
}
