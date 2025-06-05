package com.sprint.deokhugamteam7.aspect;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

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

    log.info("call → [{}]:[{}]:[{}]", className, methodName, filterArgs(args));

    try {
      Object result = joinPoint.proceed();

      long elapsedTime = System.currentTimeMillis() - start;
      log.info("success ← [{}]:[{}]:[{}] - [({}ms)]", className, methodName, abbreviateResult(result), elapsedTime);

      return result;

    } catch (Throwable e) {
      log.error("error ← [{}]:[{}]:[{}]", className, methodName, e.getMessage());
      throw e;
    }
  }

  private String filterArgs(Object[] args) {
    return Arrays.stream(args)
        .map(arg -> {
          if (arg == null) {
            return "null";
          }
          if (arg instanceof MultipartFile file) {
            return "MultipartFile(" + file.getOriginalFilename() + ")";
          }
          if (arg instanceof byte[] bytes) {
            return "byte[" + bytes.length + "]";
          }
          if (arg instanceof String str && str.length() > 100) {
            return "String(too long, " + str.length() + " chars)";
          }
          if (arg instanceof Collection<?> coll && coll.size() > 10) {
            return "Collection(size=" + coll.size() + ")";
          }
          return arg.toString();
        })
        .collect(Collectors.joining(", "));
  }

  private String abbreviateResult(Object result) {
    if (result == null) {
      return "null";
    }
    String res = result.toString();
    return res.length() > 200 ? res.substring(0, 200) + "..." : res;
  }
}
