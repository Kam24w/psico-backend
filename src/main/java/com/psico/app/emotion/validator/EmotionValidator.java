package com.psico.app.emotion.validator;

import org.springframework.stereotype.Component;

import com.psico.app.common.exception.ValidationException;
import com.psico.app.emotion.model.EmotionType;

@Component
public class EmotionValidator {

    public void validate(EmotionType type, Double intensity) {

        if (type == null) {
            throw new ValidationException(
                    "EMOTION_TYPE_REQUIRED",
                    "El tipo de emoción es obligatorio"
            );
        }

        if (intensity == null) {
            throw new ValidationException(
                    "EMOTION_INTENSITY_REQUIRED",
                    "La intensidad es obligatoria"
            );
        }

        if (intensity < 0 || intensity > 1) {
            throw new ValidationException(
                    "INVALID_INTENSITY",
                    "La intensidad debe estar entre 0 y 1"
            );
        }
    }
}