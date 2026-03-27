package com.psico.app.ai.service;

import com.psico.app.ai.client.ClienteIA;
import com.psico.app.emotion.model.TipoEmocion;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * ServicioIA
 * Orquesta la generación de respuestas combinando:
 * 1. El mensaje del usuario
 * 2. La emoción detectada (vía Strategy + Factory)
 * 3. La petición a Claude API (vía ClienteIA)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ServicioIA {

    private final ClienteIA clienteIA;
    private final GeneradorRespuesta generadorRespuesta;

    public String generarRespuesta(String mensajeUsuario, TipoEmocion emocion) {
        log.info("Generando respuesta para emoción: {}", emocion);

        String systemPrompt = generadorRespuesta.construirPromptSistema(emocion);
        String userMessage  = generadorRespuesta.construirMensajeUsuario(mensajeUsuario, emocion);

        return clienteIA.enviarMensaje(systemPrompt, userMessage);
    }
}
