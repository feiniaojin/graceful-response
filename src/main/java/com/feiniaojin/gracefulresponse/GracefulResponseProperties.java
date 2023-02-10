package com.feiniaojin.gracefulresponse;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 核心配置类.
 *
 * @author <a href="mailto:943868899@qq.com">Yujie</a>
 * @version 0.1
 * @since 0.1
 */
@ConfigurationProperties(prefix = "gr")
public class GracefulResponseProperties {

    private boolean useValidationMsg = true;

    private boolean printExceptionInGlobalAdvice = true;

    /**
     * 默认的Response实现类名称，配置了responseClassFullName，则responseStyle不生效
     */
    private String responseClassFullName;

    /**
     * responseStyle的风格，responseClassFullName为空时才会生效
     * responseStyle==null或者responseStyle==0,Response风格为 DefaultResponseImplStyle0
     * responseStyle=1,Response风格为 DefaultResponseImplStyle1
     */
    private Integer responseStyle;

    /**
     * 默认的成功返回码
     */
    private String defaultSuccessCode = "0";

    /**
     * 默认的成功提示
     */
    private String defaultSuccessMsg = "ok";

    /**
     * 默认的失败码
     */
    private String defaultFailCode = "1";

    /**
     * 默认的失败提示
     */
    private String defaultFailMsg = "error";

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

    public String getDefaultSuccessCode() {
        return defaultSuccessCode;
    }

    public void setDefaultSuccessCode(String defaultSuccessCode) {
        this.defaultSuccessCode = defaultSuccessCode;
    }

    public String getDefaultSuccessMsg() {
        return defaultSuccessMsg;
    }

    public void setDefaultSuccessMsg(String defaultSuccessMsg) {
        this.defaultSuccessMsg = defaultSuccessMsg;
    }

    public String getDefaultFailCode() {
        return defaultFailCode;
    }

    public void setDefaultFailCode(String defaultFailCode) {
        this.defaultFailCode = defaultFailCode;
    }

    public String getDefaultFailMsg() {
        return defaultFailMsg;
    }

    public void setDefaultFailMsg(String defaultFailMsg) {
        this.defaultFailMsg = defaultFailMsg;
    }

    public String getResponseClassFullName() {
        return responseClassFullName;
    }

    public void setResponseClassFullName(String responseClassFullName) {
        this.responseClassFullName = responseClassFullName;
    }

    public Integer getResponseStyle() {
        return responseStyle;
    }

    public void setResponseStyle(Integer responseStyle) {
        this.responseStyle = responseStyle;
    }
}
