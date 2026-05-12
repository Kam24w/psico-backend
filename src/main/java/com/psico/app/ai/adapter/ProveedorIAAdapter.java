package com.psico.app.ai.adapter;

public interface ProveedorIAAdapter {
    String generarRespuesta(String systemPrompt, String userMessage);
    ProveedorIA proveedor();
}
