package com.feiniaojin.gracefulresponse.test.app.service.impl;

import com.feiniaojin.gracefulresponse.test.app.exceptions.ExampleExceptions;
import com.feiniaojin.gracefulresponse.test.app.service.ExampleService;
import org.springframework.stereotype.Service;

@Service
public class ExampleServiceImpl implements ExampleService {
    @Override
    public void testUnCheckedException() {
        throw new ExampleExceptions.UnCheckedException();
    }

    @Override
    public void testCheckedException() throws Exception {
        throw new Exception();
    }

}
