package com.feiniaojin.ddd.ecosystem.gracefulresponse;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 核心配置类.
 *
 * @author <a href="mailto:943868899@qq.com">Yujie</a>
 * @version 0.1
 * @since 0.1
 */
@ConfigurationProperties(prefix = "ddd.ecosystem.gr")
public class GracefulResponseProperties {

    private boolean useValidationMsg = true;

    private boolean printExceptionInGlobalAdvice = true;

    public boolean isUseValidationMsg() {
        return useValidationMsg;
    }

    public void setUseValidationMsg(boolean useValidationMsg) {
        this.useValidationMsg = useValidationMsg;
    }

    public boolean isPrintExceptionInGlobalAdvice() {
        return printExceptionInGlobalAdvice;
    }

    public void setPrintExceptionInGlobalAdvice(boolean printExceptionInGlobalAdvice) {
        this.printExceptionInGlobalAdvice = printExceptionInGlobalAdvice;
    }
}
