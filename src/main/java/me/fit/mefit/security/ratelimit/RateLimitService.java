package me.fit.mefit.security.ratelimit;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Service
public class RateLimitService {
    private static final long LOGIN_ATTEMPTS = 3;
    private static final long REFILL_TOKENS = 1;
    private static final long DURATION_MINUTES = 1;

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    public Bucket resolveBucket(String ip) {
        return cache.computeIfAbsent(ip, this::newBucket);
    }

    private Bucket newBucket(String ip) {
        return Bucket4j.builder()
                .addLimit(Bandwidth.classic(LOGIN_ATTEMPTS, Refill.intervally(REFILL_TOKENS, Duration.ofMinutes(DURATION_MINUTES))))
                .build();
    }
}
