// package com.example.springsocial.service;

// import org.springframework.stereotype.Service;

// @Service
// public class RateLimiter {
//     //autowiring dependencies
    
//     public Bucket resolveBucket(String key) {
//         Supplier<BucketConfiguration> configSupplier = getConfigSupplierForUser(key);
        
//         // Does not always create a new bucket, but instead returns the existing one if it exists.
//         return buckets.builder().build(key, configSupplier);
//     }

//     private Supplier<BucketConfiguration> getConfigSupplierForUser(String key) {
//         User user = userRepository.findById(userId);
//         Refill refill = Refill.intervally(user.getLimit(), Duration.ofMinutes(1));
//         Bandwidth limit = Bandwidth.classic(user.getLimit(), refill);
//         return () -> (BucketConfiguration.builder()
//                 .addLimit(limit)
//                 .build());
//     }
// }