package com.example.scheduled;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledExample {

    // * * * * * *
    @Scheduled(cron = "*/5 * * * * *")
    public void countToTen(){
        for(int i=1; i<=10; i++){
            System.out.println(i + " ");
        }

    }
}
