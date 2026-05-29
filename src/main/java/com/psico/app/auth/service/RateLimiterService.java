package com.psico.app.auth.service;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimiterService {

    private static final int MAX_ATTEMPTS = 5;
    private static final int LOCK_DURATION_MINUTES = 15;

    private final ConcurrentHashMap<String, Attempt> attemptsMap = new ConcurrentHashMap<>();

    private static class Attempt {
        int count;
        LocalDateTime blockedUntil;

        Attempt(int count, LocalDateTime blockedUntil) {
            this.count = count;
            this.blockedUntil = blockedUntil;
        }
    }

    public boolean isBlocked(String ip) {
        Attempt attempt = attemptsMap.get(ip);
        if (attempt == null) {
            return false;
        }

        if (attempt.blockedUntil != null) {
            if (LocalDateTime.now().isBefore(attempt.blockedUntil)) {
                return true;
            } else {
                // El bloqueo ha expirado
                attemptsMap.remove(ip);
                return false;
            }
        }
        return false;
    }

    public void registerFailure(String ip) {
        attemptsMap.compute(ip, (key, val) -> {
            if (val == null) {
                return new Attempt(1, null);
            }
            if (val.blockedUntil != null && LocalDateTime.now().isAfter(val.blockedUntil)) {
                return new Attempt(1, null);
            }
            int newCount = val.count + 1;
            LocalDateTime blockedUntil = null;
            if (newCount >= MAX_ATTEMPTS) {
                blockedUntil = LocalDateTime.now().plusMinutes(LOCK_DURATION_MINUTES);
            }
            return new Attempt(newCount, blockedUntil);
        });
    }

    public void registerSuccess(String ip) {
        attemptsMap.remove(ip);
    }
}
