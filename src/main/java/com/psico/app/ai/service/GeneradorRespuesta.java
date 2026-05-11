package com.psico.app.ai.service;

import com.psico.app.emotion.model.TipoEmocion;
import com.psico.app.patterns.factory.FabricaEstrategia;
import com.psico.app.patterns.strategy.EstrategiaEmocion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class GeneradorRespuesta {

    private final FabricaEstrategia fabricaEstrategia;

    public String construirPromptSistema(TipoEmocion emocion, String personalidadBase) {
        EstrategiaEmocion estrategia = fabricaEstrategia.crear(emocion);
        String instruccionesEmocion = estrategia.obtenerInstruccionesSistema();

        String reglasOutput = """

                ===INSTRUCCIONES DE FORMATO - OBLIGATORIAS===
                - Responde ÚNICAMENTE con el mensaje para el usuario. Nada más.
                - PROHIBIDO escribir en inglés. Solo español.
                - PROHIBIDO razonar antes de responder (no escribas "Let's", "I'll", "Okay", "The user", etc.)
                - PROHIBIDO mostrar opciones, variantes ni comparaciones ("Option 1", "Version A", etc.)
                - PROHIBIDO usar asteriscos (*), guiones (-) ni numeración para analizar.
                - NO repitas la respuesta dos veces.
                - NO uses comillas alrededor de tu respuesta.
                - Tu respuesta debe ser directa, empática, máximo 3 oraciones en español.
                =============================================
                """;

        return instruccionesEmocion + reglasOutput +
               "\nPersonalidad: " + (personalidadBase != null ? personalidadBase : "Eres un profesional empático.");
    }

    public String construirMensajeUsuario(String mensajeOriginal, TipoEmocion emocion, String memoriaUsuario) {
        EstrategiaEmocion estrategia = fabricaEstrategia.crear(emocion);
        String mensajeContextualizado = estrategia.generarContexto(mensajeOriginal);
        
        if (memoriaUsuario != null && !memoriaUsuario.isBlank()) {
            return String.format("Memoria relevante del usuario:\n%s\n\nMensaje actual:\n%s", 
                    memoriaUsuario, mensajeContextualizado);
        }
        return mensajeContextualizado;
    }
}