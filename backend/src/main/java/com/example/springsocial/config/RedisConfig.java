// package com.example.springsocial.config;


// import javax.cache.Caching;

// import org.springframework.cache.CacheManager;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;

// import io.micrometer.core.instrument.MeterRegistry.Config;

// @Configuration
// public class RedisConfig  {
    
//     @Bean
//     public Config config() {
//         Config config = new Config();
//         ((Object) config).useSingleServer().setAddress("redis://localhost:6379");
//         return config;
//     }
    
//     @Bean
//     public CacheManager cacheManager(Config config) {
//         CacheManager manager = (CacheManager) Caching.getCachingProvider().getCacheManager();
//         cacheManager.createCache("cache", RedissonConfiguration.fromConfig(config));
//         return cacheManager;
//     }

//     @Bean
//     ProxyManager<String> proxyManager(CacheManager cacheManager) {
//         return new JCacheProxyManager<>(cacheManager.getCache("cache"));
//     }
// }