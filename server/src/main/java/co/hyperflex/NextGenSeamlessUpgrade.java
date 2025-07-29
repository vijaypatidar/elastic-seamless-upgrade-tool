package co.hyperflex;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NextGenSeamlessUpgrade {
  public static void main(String[] args) {
    SpringApplication.run(NextGenSeamlessUpgrade.class, args);
  }
}