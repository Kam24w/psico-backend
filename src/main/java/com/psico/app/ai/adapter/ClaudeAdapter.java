package com.psico.app.ai.adapter;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ClaudeAdapter implements ProveedorIAAdapter {

    @Override
    public String generarRespuesta(String systemPrompt, String userMessage) {
        log.debug("ClaudeAdapter generando respuesta alternativa");
        return "[Claude simulado] " + systemPrompt + " → " + userMessage;
    }

    @Override
    public ProveedorIA proveedor() {
        return ProveedorIA.CLAUDE;
    }
}
