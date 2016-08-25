package com.nd.resource.transform.task;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Created by way on 2016/8/24.
 */
@Configuration
public class TaskConfig {

    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(5);
        taskExecutor.setMaxPoolSize(10);
        taskExecutor.setQueueCapacity(25);
        return taskExecutor;
    }

    @Bean
    public TaskExecutorExample taskExecutorExample(ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        return new TaskExecutorExample(threadPoolTaskExecutor);
    }
}
