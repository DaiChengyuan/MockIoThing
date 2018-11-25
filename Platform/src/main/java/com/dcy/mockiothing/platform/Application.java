package com.dcy.mockiothing.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;

import java.util.concurrent.ScheduledThreadPoolExecutor;

@SpringBootApplication
@ImportResource(locations = {"classpath:application-bean.xml"})
public class Application {

    public static void main(String[] args) throws Exception {
        SpringApplication springApplication = new SpringApplication(Application.class);
        springApplication.run(args);
    }

    @Bean(name = "deviceActionExecutor")
    public ScheduledThreadPoolExecutor scheduledThreadPoolExecutor() {
        return new ScheduledThreadPoolExecutor(5);
    }
}
