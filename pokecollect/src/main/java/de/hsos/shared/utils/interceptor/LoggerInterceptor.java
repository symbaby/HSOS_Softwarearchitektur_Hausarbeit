package de.hsos.shared.utils.interceptor;


import org.jboss.logging.Logger;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.util.concurrent.TimeUnit;

/**
 * @Author: beryildi, jbelasch
 */
@Interceptor
@Loggable
public class LoggerInterceptor {

    private static final Logger logger = Logger.getLogger(LoggerInterceptor.class.getName());

    @AroundInvoke
    public Object logMethodInvocation(InvocationContext context) throws Exception {

        // TODO: Beobachten wie das mit der performance ist wegen reflections...

        String methodName = context.getMethod().getName();
        String className = context.getTarget().getClass().getName();


        long startTime = System.currentTimeMillis();
        logger.info(className + " : " + methodName + " - Method invocation started at: " + TimeUnit.MILLISECONDS.toSeconds(startTime));

        Object result = context.proceed();

        long endTime = System.currentTimeMillis();
        logger.info(className + " : " + methodName + " - Method invocation ended at: " + TimeUnit.MILLISECONDS.toSeconds(endTime));
        logger.info(className + " : " + methodName + " - Total time taken: " + (endTime - startTime) + "ms");
        return result;
    }
}
