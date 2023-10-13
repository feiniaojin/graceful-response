package com.feiniaojin.gracefulresponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;

public class Init {

    private final Logger logger = LoggerFactory.getLogger(Init.class);

    @PostConstruct
    public void init() {
        logger.info("Graceful Response:已启动");
    }
}
