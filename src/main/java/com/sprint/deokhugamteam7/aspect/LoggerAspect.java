package com.sprint.deokhugamteam7.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class LoggerAspect {

    // 공통으로 사용할지 아니면 저(성태)만 사용 할지 아직 몰라서 일단 notification 도메인만 execution 했습니다.
    @Around("execution(* com.sprint.deokhugamteam7.domain.notification.controller..*(..)) || " +
        "execution(* com.sprint.deokhugamteam7.domain.notification.service..*(..)) || " +
        "execution(* com.sprint.deokhugamteam7.domain.notification.repository..*(..))")
    public Object logMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();
        Object[] args = joinPoint.getArgs();

        log.info("🔹 호출 → {}.{}({})", className, methodName, Arrays.toString(args));

        try {
            Object result = joinPoint.proceed();

            long elapsedTime = System.currentTimeMillis() - start;
            log.info("✅ 완료 ← {}.{} 리턴: {} ({}ms)", className, methodName, result, elapsedTime);

            return result;

        } catch (Throwable e) {
            log.error("❌ 예외 ← {}.{} 예외 발생: {}", className, methodName, e.getMessage(), e);
            throw e;
        }
    }
}