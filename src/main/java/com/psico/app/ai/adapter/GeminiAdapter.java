package com.psico.app.ai.adapter;

import org.springframework.stereotype.Component;

import com.psico.app.ai.client.ClienteIA;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GeminiAdapter implements ProveedorIAAdapter {

    private final ClienteIA clienteIA;

    @Override
    public String generarRespuesta(String systemPrompt, String userMessage) {
        return clienteIA.enviarMensaje(systemPrompt, userMessage);
    }

    @Override
    public ProveedorIA proveedor() {
        return ProveedorIA.GEMINI;
    }
}
