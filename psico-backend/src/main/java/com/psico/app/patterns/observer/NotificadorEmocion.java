package com.psico.app.patterns.observer;

import com.psico.app.emotion.model.TipoEmocion;

public interface NotificadorEmocion {
    void registrarObservador(ObservadorEmocion observador);
    void notificar(Long usuarioId, TipoEmocion nuevaEmocion);
}
