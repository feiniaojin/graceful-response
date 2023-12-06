package com.feiniaojin.gracefulresponse.test.app.dto;

import jakarta.validation.constraints.NotNull;

public class ExtendProperties {
    @NotNull(message = "扩展属性property1不能为空")
    private String property1;

    public String getProperty1() {
        return property1;
    }

    public void setProperty1(String property1) {
        this.property1 = property1;
    }
}
