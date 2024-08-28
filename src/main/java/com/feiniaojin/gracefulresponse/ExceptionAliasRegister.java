package com.feiniaojin.gracefulresponse;

import com.feiniaojin.gracefulresponse.api.ExceptionAliasFor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author qinyujie
 */
public class ExceptionAliasRegister {

    private final Logger logger = LoggerFactory.getLogger(ExceptionAliasRegister.class);

    private ConcurrentHashMap<Class<? extends Throwable>, ExceptionAliasFor> aliasForMap = new ConcurrentHashMap<>();

    /**
     * 注册
     *
     * @param throwableClass
     * @return
     */
    public ExceptionAliasRegister doRegisterExceptionAlias(Class<? extends Throwable> throwableClass) {

        ExceptionAliasFor exceptionAliasFor = throwableClass.getAnnotation(ExceptionAliasFor.class);
        if (exceptionAliasFor == null) {
            logger.error("注册了未加ExceptionAliasFor的异常,throwableClass={}", throwableClass);
            throw new GracefulResponseException();
        }

        Class<? extends Throwable>[] classes = exceptionAliasFor.aliasFor();
        for (Class<? extends Throwable> c : classes) {
            aliasForMap.put(c, exceptionAliasFor);
        }

        return this;
    }

    /**
     * 获取
     *
     * @param tClazz
     * @return
     */
    public ExceptionAliasFor getExceptionAliasFor(Class<? extends Throwable> tClazz) {
        return aliasForMap.get(tClazz);
    }
}
