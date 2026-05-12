package com.psico.app.config.service;

import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Service;

@Service
public class ConfigurationService {

    public AppConfig getConfiguration() {
        return AppConfig.builder()
                .maxMessagesPerSession(50)
                .notificationsEnabled(true)
                .analysisWindowDays(30)
                .build();
    }

    @Deprecated(forRemoval = false)
    public AppConfig obtenerConfiguracion() {
        return getConfiguration();
    }

    @Builder
    @Data
    public static class AppConfig {
        private int maxMessagesPerSession;
        private boolean notificationsEnabled;
        private int analysisWindowDays;
    }
}
