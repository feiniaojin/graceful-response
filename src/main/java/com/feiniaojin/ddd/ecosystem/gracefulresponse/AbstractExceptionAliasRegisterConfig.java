package com.feiniaojin.ddd.ecosystem.gracefulresponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.Resource;

public abstract class AbstractExceptionAliasRegisterConfig implements ApplicationContextAware {

    private Logger logger = LoggerFactory.getLogger(AbstractExceptionAliasRegisterConfig.class);

    @Resource
    private ExceptionAliasRegister register;

    protected abstract void registerAlias(ExceptionAliasRegister register);

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.registerAlias(register);
    }
}
