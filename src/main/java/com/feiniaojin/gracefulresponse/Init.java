package com.feiniaojin.gracefulresponse;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author qinyujie
 */
public class Init {

    private final Logger logger = LoggerFactory.getLogger(Init.class);

    @PostConstruct
    public void init() {
        logger.info("Graceful Response:已启动");
    }
}
