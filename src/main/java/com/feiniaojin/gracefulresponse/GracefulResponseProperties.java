package com.feiniaojin.gracefulresponse;

import com.feiniaojin.gracefulresponse.defaults.DefaultConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * 核心配置类.
 *
 * @author <a href="mailto:943868899@qq.com">Yujie</a>
 * @version 0.1
 * @since 0.1
 */
@ConfigurationProperties(prefix = "graceful-response")
public class GracefulResponseProperties {

    /**
     * 在全局异常处理器中打印异常，默认不打印
     */
    private boolean printExceptionInGlobalAdvice = false;

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
    private String defaultSuccessCode = DefaultConstants.DEFAULT_SUCCESS_CODE;

    /**
     * 默认的成功提示
     */
    private String defaultSuccessMsg = DefaultConstants.DEFAULT_SUCCESS_MSG;

    /**
     * 默认的失败码
     */
    private String defaultErrorCode = DefaultConstants.DEFAULT_ERROR_CODE;

    /**
     * 默认的失败提示
     */
    private String defaultErrorMsg = DefaultConstants.DEFAULT_ERROR_MSG;

    /**
     * Validate异常码，不提供的话默认DefaultConstants.DEFAULT_ERROR_CODE
     */
    private String defaultValidateErrorCode = DefaultConstants.DEFAULT_ERROR_CODE;

    /**
     * 例外包路径
     */
    private List<String> excludePackages;

    /**
     * 不使用@ExceptionMapper和@ExceptionAliasFor修饰的原生异常
     * 是否使用异常信息Throwable类的detailMessage进行返回
     * originExceptionUsingDetailMessage=false，则msg=defaultErrorMsg
     */
    private Boolean originExceptionUsingDetailMessage = false;

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

    public String getDefaultErrorCode() {
        return defaultErrorCode;
    }

    public void setDefaultErrorCode(String defaultErrorCode) {
        this.defaultErrorCode = defaultErrorCode;
    }

    public String getDefaultErrorMsg() {
        return defaultErrorMsg;
    }

    public void setDefaultErrorMsg(String defaultErrorMsg) {
        this.defaultErrorMsg = defaultErrorMsg;
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

    public String getDefaultValidateErrorCode() {
        return defaultValidateErrorCode;
    }

    public void setDefaultValidateErrorCode(String defaultValidateErrorCode) {
        this.defaultValidateErrorCode = defaultValidateErrorCode;
    }

    public List<String> getExcludePackages() {
        return excludePackages;
    }

    public void setExcludePackages(List<String> excludePackages) {
        this.excludePackages = excludePackages;
    }

    public Boolean getOriginExceptionUsingDetailMessage() {
        return originExceptionUsingDetailMessage;
    }

    public void setOriginExceptionUsingDetailMessage(Boolean originExceptionUsingDetailMessage) {
        this.originExceptionUsingDetailMessage = originExceptionUsingDetailMessage;
    }
}
