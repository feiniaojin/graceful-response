package com.feiniaojin.gracefulresponse.defaults;


import com.feiniaojin.gracefulresponse.GracefulResponseProperties;
import com.feiniaojin.gracefulresponse.api.ResponseFactory;
import com.feiniaojin.gracefulresponse.api.ResponseStatusFactory;
import com.feiniaojin.gracefulresponse.data.Response;
import com.feiniaojin.gracefulresponse.data.ResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 提供的默认的ResponseBeanFactory实现.
 *
 * @author <a href="mailto:943868899@qq.com">Yujie</a>
 * @version 0.1
 */
public class DefaultResponseFactory implements ResponseFactory {

    private Logger logger = LoggerFactory.getLogger(DefaultResponseFactory.class);

    private static final Integer RESPONSE_STYLE_0 = 0;

    private static final Integer RESPONSE_STYLE_1 = 1;

    @Resource
    private ResponseStatusFactory responseStatusFactory;

    @Resource
    private GracefulResponseProperties properties;

    @Override
    public Class<?> responseClass() {
        try {
            String responseClassFullName = properties.getResponseClassFullName();

            if (StringUtils.hasLength(responseClassFullName)) {
                return Class.forName(responseClassFullName);
            } else {
                Integer responseStyle = properties.getResponseStyle();
                if (Objects.isNull(responseStyle) || RESPONSE_STYLE_0.equals(responseStyle)) {
                    return DefaultResponseImplStyle0.class;
                } else if (RESPONSE_STYLE_1.equals(responseStyle)) {
                    return DefaultResponseImplStyle1.class;
                } else {
                    logger.error("不支持的Response style类型,responseStyle={}", responseStyle);
                    throw new IllegalArgumentException("不支持的Response style类型");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Response newEmptyInstance() {
        try {
            return (Response) responseClass().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Response newInstance(ResponseStatus responseStatus) {
        Response bean = this.newEmptyInstance();
        bean.setStatus(responseStatus);
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
        bean.setStatus(responseStatusFactory.defaultFail());
        return bean;
    }

}
