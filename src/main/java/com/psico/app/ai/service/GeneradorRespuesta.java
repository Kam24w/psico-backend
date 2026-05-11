package com.psico.app.ai.service;

import com.psico.app.emotion.model.TipoEmocion;
import com.psico.app.patterns.factory.FabricaEstrategia;
import com.psico.app.patterns.strategy.EstrategiaEmocion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * GeneradorRespuesta
 * Construye los prompts optimizados para Gemma 4.
 */
@Component
@RequiredArgsConstructor
public class GeneradorRespuesta {

    private final FabricaEstrategia fabricaEstrategia;

    public String construirPromptSistema(TipoEmocion emocion, String personalidadBase) {
        EstrategiaEmocion estrategia = fabricaEstrategia.crear(emocion);
        String instruccionesEmocion = estrategia.obtenerInstruccionesSistema();
        String personalidad = personalidadBase != null ? personalidadBase : "Eres un profesional empático.";

        return personalidad + "\n\n" + instruccionesEmocion + """
            
            === REGLAS ESTRÍCTAS DE FORMATO ===
            - Responde ÚNICAMENTE con el mensaje directo para el usuario.
            - PROHIBIDO razonar antes de responder (no escribas "Let's", "I'll", "Draft", etc.)
            - PROHIBIDO usar asteriscos (*), guiones (-) o numeración para analizar.
            - Tu respuesta debe ser natural, en español y máximo de 3 oraciones.
            """;
    }

    public String construirMensajeUsuario(String mensajeOriginal, TipoEmocion emocion, String memoriaUsuario) {
        EstrategiaEmocion estrategia = fabricaEstrategia.crear(emocion);

        String ejemplos = """
                Ejemplos de respuesta esperada:
                Usuario: me siento muy cansado hoy
                Respuesta: Es completamente válido sentirse así. ¿Quieres contarme qué ha pasado hoy?

                Usuario: hola
                Respuesta: ¡Hola! Me alegra que estés aquí. ¿Cómo te encuentras hoy?

                Ahora responde al siguiente mensaje:
                """;

        String contextoEmocion = estrategia.generarContexto(mensajeOriginal);

        if (memoriaUsuario != null && !memoriaUsuario.isBlank()) {
            return ejemplos + "Contexto previo:\n" + memoriaUsuario + "\n\nMensaje: " + contextoEmocion;
        }
        return ejemplos + "Mensaje: " + contextoEmocion;
    }
}
