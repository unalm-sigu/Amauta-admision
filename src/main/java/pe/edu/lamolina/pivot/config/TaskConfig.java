package pe.edu.lamolina.pivot.config;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import pe.albatross.zelpers.miscelanea.PhobosException;

@EnableAsync
@Configuration
@ConfigurationProperties(prefix = "task")
public class TaskConfig implements AsyncConfigurer {

    @Value("${corePoolSize}")
    Integer corePoolSize;

    @Value("${threadName}")
    String threadName;

    @Bean
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
        pool.setCorePoolSize(this.corePoolSize);
        pool.setThreadNamePrefix(this.threadName);
        pool.setWaitForTasksToCompleteOnShutdown(true);
        return pool;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new AsyncUncaughtExceptionHandler() {
            @Override
            public void handleUncaughtException(Throwable thrwbl, Method method, Object... os) {
                thrwbl.printStackTrace();
                throw new PhobosException("Error Async Uncaught Ex");
            }
        };

    }

}
