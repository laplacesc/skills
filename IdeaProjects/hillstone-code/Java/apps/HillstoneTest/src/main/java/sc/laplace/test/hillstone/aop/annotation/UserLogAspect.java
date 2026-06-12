package sc.laplace.test.hillstone.aop.annotation;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * @author jxwu
 */
@Slf4j
@Aspect
@Component
public class UserLogAspect {

    @Around("@annotation(userLogAnnotation)")
    public Object around(ProceedingJoinPoint pjp, UserLogAnnotation userLogAnnotation) throws Throwable {
        // log - before method
        log.info("[before] execute method: {}", pjp.getSignature().getName());
        // call method
        Object result = pjp.proceed();
        // log - after method
        log.info("[after] execute method: {}, return value: {}", pjp.getSignature().getName(), result);
        return result;
    }
}
