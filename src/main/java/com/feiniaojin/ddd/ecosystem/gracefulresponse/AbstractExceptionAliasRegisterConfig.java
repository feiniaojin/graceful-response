package com.feiniaojin.ddd.ecosystem.gracefulresponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;

import javax.annotation.Resource;

public abstract class AbstractExceptionAliasRegisterConfig implements ApplicationContextAware {

    private Logger logger = LoggerFactory.getLogger(AbstractExceptionAliasRegisterConfig.class);

    protected abstract void registerAlias(ExceptionAliasRegister register);

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {

        try {
            ExceptionAliasRegister aliasRegister = applicationContext.getBean(ExceptionAliasRegister.class);
            if (aliasRegister == null) {
                logger.warn("未从ApplicationContext中获取到ExceptionAliasRegister实例， @ExceptionAliasFor注解将无效");
                return;
            }
            this.registerAlias(aliasRegister);
        } catch (Exception e) {
            logger.warn("未从ApplicationContext中获取到ExceptionAliasRegister实例， @ExceptionAliasFor注解将无效", e);
        }
    }


}
