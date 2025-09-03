package co.hyperflex.precheck.runner;

import java.util.concurrent.Executor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.VirtualThreadTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
public class PrecheckExecutorConfig {

  @Bean(name = "precheckAsyncExecutor")
  public Executor precheckAsyncExecutor() {
    return new VirtualThreadTaskExecutor("Precheck-");
  }
}
