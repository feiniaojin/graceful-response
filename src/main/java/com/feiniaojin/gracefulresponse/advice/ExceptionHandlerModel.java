package com.feiniaojin.gracefulresponse.advice;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 异常和Handler绑定模型
 * @author qinyujie
 */
public class ExceptionHandlerModel {

    private Method method;
    private Object bean;

    public ExceptionHandlerModel(Method method, Object bean) {
        this.method = method;
        this.bean = bean;
    }

    public Object getBean() {
        return bean;
    }

    public void setBean(Object bean) {
        this.bean = bean;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ExceptionHandlerModel that = (ExceptionHandlerModel) o;
        return Objects.equals(method, that.method) && Objects.equals(bean, that.bean);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, bean);
    }
}
