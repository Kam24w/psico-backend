package com.psico.app.emotion.validator;

import org.springframework.stereotype.Component;

import com.psico.app.common.exception.ValidationException;
import com.psico.app.emotion.model.TipoEmocion;

@Component
public class EmotionValidator {

    public void validate(TipoEmocion tipo, Double intensidad) {

        if (tipo == null) {
            throw new ValidationException(
                    "EMOTION_TYPE_REQUIRED",
                    "El tipo de emoción es obligatorio"
            );
        }

        if (intensidad == null) {
            throw new ValidationException(
                    "EMOTION_INTENSITY_REQUIRED",
                    "La intensidad es obligatoria"
            );
        }

        if (intensidad < 0 || intensidad > 1) {
            throw new ValidationException(
                    "INVALID_INTENSITY",
                    "La intensidad debe estar entre 0 y 1"
            );
        }
    }
}