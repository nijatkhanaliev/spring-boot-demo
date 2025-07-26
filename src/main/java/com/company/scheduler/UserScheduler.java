package com.company.scheduler;

import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserScheduler {

    @SchedulerLock(name = "TaskScheduler_scheduledTask",
            lockAtLeastFor = "PT5S", lockAtMostFor = "PT10S")
    @Scheduled(cron = "0 0 10 ? * MON-FRI")
    public void printHello(){
        log.info("{}: hello", Thread.currentThread().getName());
    }
}
