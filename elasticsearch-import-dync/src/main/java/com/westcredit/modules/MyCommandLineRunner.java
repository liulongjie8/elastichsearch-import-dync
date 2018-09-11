package com.westcredit.modules;

import com.westcredit.modules.service.SyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(value = 1)
public class MyCommandLineRunner implements ApplicationRunner {

    @Autowired
    private SyncService service;

    @Override
    public void run(ApplicationArguments var1) throws Exception{
        service.schedlude();
    }
}
