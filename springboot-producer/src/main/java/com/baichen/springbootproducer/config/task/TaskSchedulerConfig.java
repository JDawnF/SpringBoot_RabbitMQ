package com.baichen.springbootproducer.config.task;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 定时任务配置类
 */

@Configuration
@EnableScheduling
public class TaskSchedulerConfig implements SchedulingConfigurer {

    // 设置线程池
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskScheduler());
    }

    @Bean(destroyMethod="shutdown")  // 在应用服务关闭后，调用shutdown方法
    public Executor taskScheduler(){
        return Executors.newScheduledThreadPool(100);
    }

}

