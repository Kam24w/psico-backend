package com.psico.app.emotion.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class EmotionTypeConverter implements AttributeConverter<EmotionType, String> {

    @Override
    public String convertToDatabaseColumn(EmotionType attribute) {
        if (attribute == null) {
            return null;
        }
        return switch (attribute) {
            case HAPPY -> "FELIZ";
            case SAD -> "TRISTE";
            case STRESSED -> "ESTRESADO";
            case ANGRY -> "ENOJADO";
            case ANXIOUS -> "ANSIOSO";
            case SURPRISED -> "SORPRENDIDO";
            case NEUTRAL -> "NEUTRAL";
        };
    }

    @Override
    public EmotionType convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return switch (dbData.toUpperCase()) {
            case "FELIZ" -> EmotionType.HAPPY;
            case "TRISTE" -> EmotionType.SAD;
            case "ESTRESADO" -> EmotionType.STRESSED;
            case "ENOJADO" -> EmotionType.ANGRY;
            case "ANSIOSO" -> EmotionType.ANXIOUS;
            case "SORPRENDIDO" -> EmotionType.SURPRISED;
            case "NEUTRAL" -> EmotionType.NEUTRAL;
            default -> EmotionType.NEUTRAL;
        };
    }
}
