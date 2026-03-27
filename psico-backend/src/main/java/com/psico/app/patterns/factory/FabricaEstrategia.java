package com.psico.app.patterns.factory;

import com.psico.app.emotion.model.TipoEmocion;
import com.psico.app.patterns.strategy.EstrategiaEmocion;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * PATRÓN FACTORY
 * Crea dinámicamente la estrategia correcta según la emoción detectada.
 * Centraliza la lógica de selección de estrategia.
 */
@Component
@RequiredArgsConstructor
public class FabricaEstrategia {

    private final ApplicationContext context;

    public EstrategiaEmocion crear(TipoEmocion emocion) {
        if (emocion == null) {
            return context.getBean("estrategiaNeutral", EstrategiaEmocion.class);
        }

        return switch (emocion) {
            case FELIZ, SORPRENDIDO -> context.getBean("estrategiaFeliz", EstrategiaEmocion.class);
            case TRISTE             -> context.getBean("estrategiaTriste", EstrategiaEmocion.class);
            case ESTRESADO, ANSIOSO, ENOJADO -> context.getBean("estrategiaEstresado", EstrategiaEmocion.class);
            default                 -> context.getBean("estrategiaNeutral", EstrategiaEmocion.class);
        };
    }
}
