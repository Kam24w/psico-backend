package com.psico.app.patterns.state;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.psico.app.emotion.model.TipoEmocion;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UsuarioEstadoContext {

    private final ApplicationContext context;

    public EstadoEmocional obtenerEstado(TipoEmocion emocion) {
        if (emocion == null) {
            return context.getBean("calmadoState", EstadoEmocional.class);
        }

        return switch (emocion) {
            case FELIZ, SORPRENDIDO -> context.getBean("motivadoState", EstadoEmocional.class);
            case TRISTE -> context.getBean("ansiosoState", EstadoEmocional.class);
            case ANSIOSO, ESTRESADO, ENOJADO -> context.getBean("crisisState", EstadoEmocional.class);
            default -> context.getBean("calmadoState", EstadoEmocional.class);
        };
    }
}
