package com.sprint.deokhugamteam7.aspect;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LoggerAspect {

    @Around("execution(* com.sprint.deokhugamteam7.domain..controller..*(..)) || " +
        "execution(* com.sprint.deokhugamteam7.domain..service..*(..)) || " +
        "execution(* com.sprint.deokhugamteam7.domain..repository..*(..))")
    public Object logMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();
        Object[] args = joinPoint.getArgs();

        log.info("call → [{}]:[{}]:[{}])", className, methodName, Arrays.toString(args));

        try {
            Object result = joinPoint.proceed();

            long elapsedTime = System.currentTimeMillis() - start;
            log.info("success ← [{}]:[{}]:[{}] - [({}ms)]", className, methodName, result, elapsedTime);

            return result;

        } catch (Throwable e) {
            log.error("error ← [{}]:[{}]:[{}]", className, methodName, e.getMessage());
            throw e;
        }
    }
}
