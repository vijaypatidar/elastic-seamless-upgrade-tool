package co.hyperflex.configs;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableCaching
@Configuration
public class CacheConfig {
  private static final Logger log = LoggerFactory.getLogger(CacheConfig.class);

  @Bean
  public CacheManager cacheManager() {
    SimpleCacheManager manager = new SimpleCacheManager();
    manager.setCaches(List.of(
        getElasticClientCache(),
        getSettingCache()
    ));
    return manager;
  }

  private static CaffeineCache getElasticClientCache() {
    return new CaffeineCache("elasticClientCache",
        Caffeine.newBuilder().expireAfterAccess(20, TimeUnit.MINUTES).removalListener((Object key, Object value, RemovalCause cause) -> {
          if (value instanceof AutoCloseable autoCloseable) {
            try {
              autoCloseable.close();
              log.info("ElasticClient cache has been closed");
            } catch (Exception e) {
              log.warn("Error closing AutoCloseable", e);
            }
          }
        }).build());
  }

  private static CaffeineCache getSettingCache() {
    return new CaffeineCache("settingCache",
        Caffeine.newBuilder().expireAfterAccess(10, TimeUnit.MINUTES).build());
  }

}
