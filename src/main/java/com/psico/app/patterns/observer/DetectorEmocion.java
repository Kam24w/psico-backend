package com.psico.app.patterns.observer;

import com.psico.app.emotion.model.TipoEmocion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * PATRÓN OBSERVER - DetectorEmocion
 * Recibe la emoción desde el frontend y registra el estado actual por usuario.
 */
@Component
@Slf4j
public class DetectorEmocion {

    // Estado emocional actual por usuario (en memoria)
    private final Map<Long, TipoEmocion> estadoActual = new ConcurrentHashMap<>();

    public void actualizarEmocion(Long usuarioId, TipoEmocion emocion) {
        TipoEmocion anterior = estadoActual.get(usuarioId);
        estadoActual.put(usuarioId, emocion);

        if (anterior != null && anterior != emocion) {
            log.info("Cambio emocional detectado - Usuario {}: {} → {}", usuarioId, anterior, emocion);
        }
    }

    public TipoEmocion getEstadoActual(Long usuarioId) {
        return estadoActual.getOrDefault(usuarioId, TipoEmocion.NEUTRAL);
    }

    public boolean huboCambio(Long usuarioId, TipoEmocion nuevaEmocion) {
        TipoEmocion actual = estadoActual.get(usuarioId);
        return actual == null || actual != nuevaEmocion;
    }
}


/**
 * PATRÓN OBSERVER - NotificadorEmocion
 * Notifica a todos los observadores cuando cambia el estado emocional.
 */
@Component
@Slf4j
class NotificadorEmocionImpl implements NotificadorEmocion {

    private final List<ObservadorEmocion> observadores = new ArrayList<>();
    private final DetectorEmocion detector;

    public NotificadorEmocionImpl(DetectorEmocion detector) {
        this.detector = detector;
    }

    @Override
    public void registrarObservador(ObservadorEmocion observador) {
        observadores.add(observador);
    }

    @Override
    public void notificar(Long usuarioId, TipoEmocion nuevaEmocion) {
        if (detector.huboCambio(usuarioId, nuevaEmocion)) {
            detector.actualizarEmocion(usuarioId, nuevaEmocion);
            log.info("Notificando cambio emocional a {} observadores", observadores.size());
            for (ObservadorEmocion obs : observadores) {
                obs.onCambioEmocion(usuarioId, nuevaEmocion);
            }
        }
    }
}
