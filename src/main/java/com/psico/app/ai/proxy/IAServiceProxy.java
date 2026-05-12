package com.psico.app.ai.proxy;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.psico.app.ai.adapter.IAProviderSelector;
import com.psico.app.ai.adapter.ProveedorIA;
import com.psico.app.ai.adapter.ProveedorIAAdapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class IAServiceProxy {

    private final IAProviderSelector providerSelector;
    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();
    private final Map<ProveedorIA, RateLimit> limites = new ConcurrentHashMap<>();

    public String generarRespuesta(ProveedorIA proveedor, String systemPrompt, String userMessage) {
        String key = proveedor.name() + ":" + systemPrompt.hashCode() + ":" + userMessage.hashCode();
        if (cache.containsKey(key)) {
            CacheEntry entry = cache.get(key);
            if (Instant.now().isBefore(entry.expiration)) {
                log.debug("Cache IA hit para proveedor {}", proveedor);
                return entry.value;
            }
        }

        ProveedorIAAdapter adaptador = providerSelector.seleccionar(proveedor);
        verificarRateLimit(adaptador.proveedor());
        String respuesta = adaptador.generarRespuesta(systemPrompt, userMessage);
        cache.put(key, new CacheEntry(respuesta, Instant.now().plusSeconds(120)));
        return respuesta;
    }

    private void verificarRateLimit(ProveedorIA proveedor) {
        RateLimit limite = limites.computeIfAbsent(proveedor, p -> new RateLimit(0, Instant.now()));
        Instant ahora = Instant.now();
        if (ahora.isAfter(limite.windowEnd)) {
            limite.count = 0;
            limite.windowEnd = ahora.plusSeconds(60);
        }
        if (limite.count >= 20) {
            throw new IllegalStateException("Límite de solicitudes a IA alcanzado para " + proveedor);
        }
        limite.count++;
        log.debug("Solicitudes IA {} restantes: {}", proveedor, 20 - limite.count);
    }

    private static class CacheEntry {
        private final String value;
        private final Instant expiration;

        private CacheEntry(String value, Instant expiration) {
            this.value = value;
            this.expiration = expiration;
        }
    }

    private static class RateLimit {
        private int count;
        private Instant windowEnd;

        private RateLimit(int count, Instant windowEnd) {
            this.count = count;
            this.windowEnd = windowEnd;
        }
    }
}
