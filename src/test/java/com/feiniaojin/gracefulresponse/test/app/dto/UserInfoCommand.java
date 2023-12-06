package com.feiniaojin.gracefulresponse.test.app.dto;

import com.feiniaojin.gracefulresponse.api.ValidationStatusCode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;


public class UserInfoCommand {
    @NotNull(message = "userId is null !")
    private Long userId;

    @NotNull(message = "userName is null !")
    @Length(min = 6, max = 12)
    @ValidationStatusCode(code = "520")
    private String userName;

    @NotNull(message = "extendProperties is null !")
    @Valid
    private ExtendProperties extendProperties;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public ExtendProperties getExtendProperties() {
        return extendProperties;
    }

    public void setExtendProperties(ExtendProperties extendProperties) {
        this.extendProperties = extendProperties;
    }
}
