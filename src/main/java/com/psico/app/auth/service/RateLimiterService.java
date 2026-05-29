package com.psico.app.auth.service;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimiterService {

    private static final int MAX_ATTEMPTS = 5;
    private static final int LOCK_DURATION_MINUTES = 15;

    /**
     * IPs that are never subject to rate limiting (e.g. the app owner / admin testing locally).
     * Add any additional trusted IPs here.
     */
    private static final Set<String> WHITELISTED_IPS = Set.of(
        "127.0.0.1",
        "::1",
        "0:0:0:0:0:0:0:1",
        "localhost"
    );

    private final ConcurrentHashMap<String, Attempt> attemptsMap = new ConcurrentHashMap<>();

    private static class Attempt {
        int count;
        LocalDateTime blockedUntil;

        Attempt(int count, LocalDateTime blockedUntil) {
            this.count = count;
            this.blockedUntil = blockedUntil;
        }
    }

    /**
     * Returns true if the IP is currently blocked.
     * Whitelisted IPs are never blocked.
     */
    public boolean isBlocked(String ip) {
        if (WHITELISTED_IPS.contains(ip)) {
            return false;
        }

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

    /**
     * Registers a failed login attempt.
     * Whitelisted IPs are ignored.
     */
    public void registerFailure(String ip) {
        if (WHITELISTED_IPS.contains(ip)) {
            return;
        }

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

    /**
     * Clears all failed attempts for the given IP (called on successful login).
     */
    public void registerSuccess(String ip) {
        attemptsMap.remove(ip);
    }

    /**
     * Manually clears any active block for the given IP.
     * Useful for admin/owner to unblock themselves without waiting.
     */
    public void clearBlock(String ip) {
        attemptsMap.remove(ip);
    }
}
