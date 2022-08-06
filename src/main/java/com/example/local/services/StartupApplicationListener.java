package com.example.local.services;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class StartupApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    SearchingService searchingService;

    @SneakyThrows
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
//        searchingService.searchPhotos();
    }

//    @SneakyThrows
//    @Scheduled(fixedRate = 30000)
//    public void scheduledSearch(){
//        searchingService.searchPhotos();
//    }
}
