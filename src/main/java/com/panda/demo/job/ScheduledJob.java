package com.panda.demo.job;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Panda
 * @date 11/28/2019
 */
@Component
public class ScheduledJob {

    @Scheduled(fixedDelay=5000)
    public void fixedDelayJob() throws InterruptedException {
        Thread.sleep(3000L);
        System.out.println(new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date())+" >>fixedDelay执行....");
    }

    @Scheduled(fixedRate=5000)
    public void fixedRateJob() throws InterruptedException {
        Thread.sleep(3000L);
        System.out.println(new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date())+" >>fixedRate执行....");
    }
}
