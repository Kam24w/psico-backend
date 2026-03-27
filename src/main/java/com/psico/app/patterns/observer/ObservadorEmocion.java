package com.psico.app.patterns.observer;

import com.psico.app.emotion.model.TipoEmocion;

/**
 * PATRÓN OBSERVER
 * Define el contrato para observadores de cambios emocionales.
 */
public interface ObservadorEmocion {
    void onCambioEmocion(Long usuarioId, TipoEmocion nuevaEmocion);
}
