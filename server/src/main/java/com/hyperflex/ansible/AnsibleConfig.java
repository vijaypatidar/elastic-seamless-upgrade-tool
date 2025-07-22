package com.hyperflex.ansible;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AnsibleConfig {
  @Bean
  @Qualifier("ansibleTaskExecutor")
  Executor ansibleTaskExecutor() {
    return Executors.newVirtualThreadPerTaskExecutor();
  }

  @Bean
  @Qualifier("ansiblePlayBookExecutor")
  Executor ansiblePlayBookExecutor() {
    return Executors.newFixedThreadPool(10);
  }
}
