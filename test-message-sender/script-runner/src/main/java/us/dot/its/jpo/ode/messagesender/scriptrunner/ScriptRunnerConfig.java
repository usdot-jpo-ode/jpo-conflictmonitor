package us.dot.its.jpo.ode.messagesender.scriptrunner;

import org.springframework.boot.task.TaskSchedulerBuilder;
import org.springframework.boot.task.ThreadPoolTaskSchedulerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
// @EnableScheduling
public class ScriptRunnerConfig  {


    @Bean(destroyMethod = "shutdown")
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        var builder = new ThreadPoolTaskSchedulerBuilder();
        return builder
            .poolSize(3)
            .threadNamePrefix("ScriptRunner-")
            .awaitTermination(true)
            .build();
    
    }

    
    
}
