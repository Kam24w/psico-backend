package com.psico.app.ai.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * AIProviderFactory
 * Patrón Factory para seleccionar el proveedor de Inteligencia Artificial (Groq, Gemini, etc.)
 * dinámicamente en base a la configuración de la aplicación.
 */
@Component
public class AIProviderFactory {

    private static final Logger logger = LoggerFactory.getLogger(AIProviderFactory.class);

    private final Map<String, AIClient> providers;
    private final String defaultProvider;

    public AIProviderFactory(
            List<AIClient> aiClientList,
            @Value("${ai.provider:groq}") String defaultProvider
    ) {
        this.defaultProvider = defaultProvider.toLowerCase();
        
        // Maps simple class name to implementation.
        // E.g.: GroqAiAdapter -> groq, GeminiAiAdapter -> gemini
        this.providers = aiClientList.stream()
                .collect(Collectors.toMap(
                        client -> extractProviderName(client.getClass().getSimpleName()),
                        Function.identity()
                ));
                
        logger.info("AIProviderFactory initialized with providers: {}", providers.keySet());
        logger.info("Default AI provider configured as: {}", this.defaultProvider);
    }

    public AIClient getProvider() {
        return getProvider(defaultProvider);
    }

    public AIClient getProvider(String providerName) {
        AIClient provider = providers.get(providerName.toLowerCase());
        
        if (provider == null) {
            logger.warn("AI Provider '{}' not found. Using default or first available.", providerName);
            // Fallback
            if (providers.containsKey("groq")) {
                return providers.get("groq");
            }
            return providers.values().iterator().next();
        }
        
        return provider;
    }

    private String extractProviderName(String className) {
        if (className.toLowerCase().contains("groq")) return "groq";
        if (className.toLowerCase().contains("gemini")) return "gemini";
        if (className.toLowerCase().contains("openai")) return "openai";
        return className.toLowerCase();
    }
}
