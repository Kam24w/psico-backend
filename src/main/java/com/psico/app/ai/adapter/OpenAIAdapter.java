package com.psico.app.ai.adapter;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class OpenAIAdapter implements ProveedorIAAdapter {

    @Override
    public String generarRespuesta(String systemPrompt, String userMessage) {
        log.debug("OpenAIAdapter generando respuesta para prompt corto");
        return "[OpenAI simulado] " + systemPrompt + " → " + userMessage;
    }

    @Override
    public ProveedorIA proveedor() {
        return ProveedorIA.OPENAI;
    }
}
