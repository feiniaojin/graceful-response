package com.feiniaojin.gracefulresponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 带数据的异常响应
 * @author twolf
 */
public class GracefulResponseDataException extends GracefulResponseException {
    private final Logger logger = LoggerFactory.getLogger(GracefulResponseDataException.class);

    /**
     * 响应数据
     */
    private Object data;

    public GracefulResponseDataException(){
    }

    public GracefulResponseDataException(Object data){
        this.data = data;
    }

    public GracefulResponseDataException(String msg,Object data){
        super(msg);
        this.data = data;
    }

    public GracefulResponseDataException(String code,String msg,Object data){
        super(code,msg);
        this.data = data;
    }

    public GracefulResponseDataException(String msg,Throwable cause,Object data){
        super(msg,cause);
        this.data = data;
    }

    public GracefulResponseDataException(String code,String msg,Throwable cause,Object data){
        super(code,msg,cause);
        this.data = data;
    }

    public Object getData(){
        return data;
    }
}
