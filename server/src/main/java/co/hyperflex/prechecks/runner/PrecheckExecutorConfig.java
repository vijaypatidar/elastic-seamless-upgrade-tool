package co.hyperflex.prechecks.runner;

import java.util.concurrent.Executor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@Async
public class PrecheckExecutorConfig {
  @Bean(name = "precheckAsyncExecutor")
  public Executor precheckAsyncExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(4);
    executor.setMaxPoolSize(6);
    executor.setThreadNamePrefix("Precheck-");
    executor.initialize();
    return executor;
  }
}
