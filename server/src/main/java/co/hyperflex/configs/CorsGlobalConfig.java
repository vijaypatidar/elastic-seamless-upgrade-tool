package co.hyperflex.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsGlobalConfig {

  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("http://localhost:3000",
                "http://localhost:5173") // or "*" for all origins
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS").allowedHeaders("*")
            .allowCredentials(true).maxAge(3600);
      }
    };
  }
}
