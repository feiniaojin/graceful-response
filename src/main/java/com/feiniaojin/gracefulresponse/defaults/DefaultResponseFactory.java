package com.feiniaojin.gracefulresponse.defaults;


import com.feiniaojin.gracefulresponse.GracefulResponseException;
import com.feiniaojin.gracefulresponse.GracefulResponseProperties;
import com.feiniaojin.gracefulresponse.api.ResponseFactory;
import com.feiniaojin.gracefulresponse.api.ResponseStatusFactory;
import com.feiniaojin.gracefulresponse.data.Response;
import com.feiniaojin.gracefulresponse.data.ResponseStatus;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * 提供的默认的ResponseBeanFactory实现.
 *
 * @author <a href="mailto:943868899@qq.com">Yujie</a>
 * @version 0.1
 */
public class DefaultResponseFactory implements ResponseFactory {

    private final Logger logger = LoggerFactory.getLogger(DefaultResponseFactory.class);

    private static final Integer RESPONSE_STYLE_0 = 0;

    private static final Integer RESPONSE_STYLE_1 = 1;

    @Resource
    private ResponseStatusFactory responseStatusFactory;

    @Resource
    private GracefulResponseProperties properties;

    @Override
    public Response newEmptyInstance() {
        try {
            String responseClassFullName = properties.getResponseClassFullName();
            //配置了Response的全限定名，即自定义了Response，用配置的进行返回
            if (StringUtils.hasLength(responseClassFullName)) {
                Object newInstance = Class.forName(responseClassFullName).getConstructor().newInstance();
                return (Response) newInstance;
            } else {
                //没有配Response的全限定名，则创建DefaultResponse
                return generateDefaultResponse();
            }
        } catch (Exception e) {
            throw new GracefulResponseException("创建空的Response失败", e);
        }
    }

    private Response generateDefaultResponse() {
        Integer responseStyle = properties.getResponseStyle();
        //默认的Response style，5.0以上是Style0，5.0（包括5.0）之后是Style1
        if (Objects.isNull(responseStyle)) {
            return new DefaultResponseImplStyle1();
        }
        if (RESPONSE_STYLE_0.equals(responseStyle)) {
            return new DefaultResponseImplStyle0();
        } else if (RESPONSE_STYLE_1.equals(responseStyle)) {
            return new DefaultResponseImplStyle1();
        } else {
            logger.error("不支持的Response style类型,responseStyle={}", responseStyle);
            throw new IllegalArgumentException("不支持的Response style类型");
        }
    }

    @Override
    public Response newInstance(ResponseStatus responseStatus) {
        Response bean = this.newEmptyInstance();
        bean.setStatus(responseStatus);
        return bean;
    }

    @Override
    public Response newInstance(ResponseStatus statusLine, Object data) {
        Response bean = this.newInstance(statusLine);
        bean.setPayload(data);
        return bean;
    }

    @Override
    public Response newSuccessInstance() {
        Response emptyInstance = this.newEmptyInstance();
        emptyInstance.setStatus(responseStatusFactory.defaultSuccess());
        return emptyInstance;
    }

    @Override
    public Response newSuccessInstance(Object payload) {
        Response bean = this.newSuccessInstance();
        bean.setPayload(payload);
        return bean;
    }

    @Override
    public Response newFailInstance() {
        Response bean = this.newEmptyInstance();
        bean.setStatus(responseStatusFactory.defaultError());
        return bean;
    }

}
