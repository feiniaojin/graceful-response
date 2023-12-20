package com.feiniaojin.gracefulresponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public abstract class AbstractExceptionAliasRegisterConfig implements ApplicationContextAware {

    private final Logger logger = LoggerFactory.getLogger(AbstractExceptionAliasRegisterConfig.class);

    protected abstract void registerAlias(ExceptionAliasRegister register);

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {

        try {
            ExceptionAliasRegister aliasRegister = applicationContext.getBean(ExceptionAliasRegister.class);
            this.registerAlias(aliasRegister);
        } catch (Exception e) {
            logger.warn("未从ApplicationContext中获取到ExceptionAliasRegister实例， @ExceptionAliasFor注解将无效", e);
        }
    }


}
