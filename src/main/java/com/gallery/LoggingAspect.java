package com.gallery;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Component
@Aspect
public class LoggingAspect {

    @Autowired
    private Logger logger;

    @Value("${gallery.log.execution}")
    private boolean logExecution;

    @Before("execution(* com.gallery..*(..))")
    private void logExecution(JoinPoint joinPoint) {
        //logger.debug(joinPoint.toShortString());
        //!FIXME
        if (logger != null && logExecution)
            logger.debug(joinPoint.toShortString());
    }

}
