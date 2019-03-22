package com.gallery;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
@Aspect
public class LoggingAspect {

    @Autowired
    private Logger logger;

    @Before("execution(* com.gallery..*(..))")
    private void logExecution(JoinPoint joinPoint) {
        //logger.debug(joinPoint.toShortString());
        //!FIXME
        if (logger != null)
            logger.debug(joinPoint.toShortString());
    }

}
