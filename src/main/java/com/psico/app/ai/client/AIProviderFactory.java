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

    private final Map<String, ClienteIA> providers;
    private final String defaultProvider;

    public AIProviderFactory(
            List<ClienteIA> clienteIAList,
            @Value("${ai.provider:groq}") String defaultProvider
    ) {
        this.defaultProvider = defaultProvider.toLowerCase();
        
        // Mapea el nombre simple de la clase a la implementación.
        // Ej: GroqAiAdapter -> groq, GeminiAiAdapter -> gemini
        this.providers = clienteIAList.stream()
                .collect(Collectors.toMap(
                        client -> extractProviderName(client.getClass().getSimpleName()),
                        Function.identity()
                ));
                
        logger.info("AIProviderFactory inicializado con los proveedores: {}", providers.keySet());
        logger.info("Proveedor AI por defecto configurado como: {}", this.defaultProvider);
    }

    public ClienteIA getProvider() {
        return getProvider(defaultProvider);
    }

    public ClienteIA getProvider(String providerName) {
        ClienteIA provider = providers.get(providerName.toLowerCase());
        
        if (provider == null) {
            logger.warn("Proveedor IA '{}' no encontrado. Se utilizará el proveedor por defecto o el primero disponible.", providerName);
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
