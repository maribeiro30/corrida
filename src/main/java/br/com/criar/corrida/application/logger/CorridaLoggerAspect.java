package br.com.criar.corrida.application.logger;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.util.StopWatch;

@Aspect
@EnableAspectJAutoProxy
@Configuration
public class CorridaLoggerAspect {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private StopWatch stopWatch;

    @Pointcut("@annotation(Loggable)")
    public void methodLoggable() {}

    @Pointcut("within(br.com.criar.corrida..*) && !within(br.com.criar.corrida.application..*)")
    public void anyPublicMethod() {}

    @Before("methodLoggable()")
    public void beforeLoggable(JoinPoint joinPoint)  throws NoSuchMethodException{
        logger.info("Call Loggable     " + this.generateMethodCallDescription(joinPoint)
                + ReflectionHelper.generateMethodArgumentsDescription(joinPoint));
    }

    @Before("anyPublicMethod()")
    public void before(JoinPoint joinPoint)  throws NoSuchMethodException{
        logger.info("Call      " + this.generateMethodCallDescription(joinPoint)
                + ReflectionHelper.generateMethodArgumentsDescription(joinPoint));
    }

    @AfterReturning(value = "anyPublicMethod()()", returning = "returnValue")
    public void logServiceReturn(JoinPoint joinPoint, Object returnValue) throws NoSuchMethodException {
        logger.info("Return    " + this.generateMethodCallDescription(joinPoint,returnValue)
                + ReflectionHelper.generateMethodArgumentsDescription(joinPoint) + " - Success");
    }

    @AfterThrowing(value = "anyPublicMethod()", throwing = "ex")
    public void logPublicMethodException(JoinPoint joinPoint, Throwable ex)
            throws NoSuchMethodException {
        logger.error("Exception " + this.generateMethodCallDescription(joinPoint) + " - Error - "
                + ex.getClass().getSimpleName() + " - " + ex.getMessage(), ex);
    }

    @SuppressWarnings("rawtypes")
    private String generateMethodCallDescription(JoinPoint joinPoint) throws NoSuchMethodException {
        StringBuilder builder = new StringBuilder();

        Class aClass = joinPoint.getSignature().getDeclaringType();
        MethodSignature method = (MethodSignature) joinPoint.getSignature();

        String className = aClass.getSimpleName();
        String methodName = method.getName();

        builder.append(className).append(".").append(methodName);

        return builder.toString();
    }

    private String generateMethodCallDescription(JoinPoint joinPoint,Object returnValue) throws NoSuchMethodException {
        StringBuilder builder = new StringBuilder();

        Class aClass = joinPoint.getSignature().getDeclaringType();
        MethodSignature method = (MethodSignature) joinPoint.getSignature();

        String className = aClass.getSimpleName();
        String methodName = method.getName();

        builder.append(className).append(".").append(methodName);
        if(returnValue != null)
            builder.append(" RETURN : " + returnValue.toString());

        return builder.toString();
    }

    @Override
    public String toString() {
        return "CorridaLoggerAspect{ stopWatch=" + stopWatch + '}';
    }

}
