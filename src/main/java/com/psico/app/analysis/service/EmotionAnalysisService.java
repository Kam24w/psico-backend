package com.psico.app.analysis.service;

import com.psico.app.analysis.model.AnalisisEmocional;
import com.psico.app.analysis.repository.AnalisisEmocionalRepository;
import com.psico.app.conversation.model.Message;
import com.psico.app.emotion.model.TipoEmocion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmotionAnalysisService {

    private final AnalisisEmocionalRepository analisisRepository;

    @Transactional
    public AnalisisEmocional analizarConversacion(Long usuarioId, List<Message> mensajes) {
        long sentimientosPositivos = mensajes.stream()
                .filter(m -> m.getEmocionAsociada() == TipoEmocion.FELIZ || m.getEmocionAsociada() == TipoEmocion.SORPRENDIDO)
                .count();

        long sentimientosNegativos = mensajes.stream()
                .filter(m -> m.getEmocionAsociada() == TipoEmocion.TRISTE || m.getEmocionAsociada() == TipoEmocion.ESTRESADO || m.getEmocionAsociada() == TipoEmocion.ANSIOSO || m.getEmocionAsociada() == TipoEmocion.ENOJADO)
                .count();

        AnalisisEmocional analisis = AnalisisEmocional.builder()
                .usuarioId(usuarioId)
                .mensajesAnalizados(mensajes.size())
                .positivos((int) sentimientosPositivos)
                .negativos((int) sentimientosNegativos)
                .fecha(LocalDateTime.now())
                .build();

        return analisisRepository.save(analisis);
    }

    public double obtenerTendencia(List<AnalisisEmocional> historico) {
        if (historico.isEmpty()) {
            return 0.0;
        }
        return historico.stream()
                .mapToDouble(a -> a.getPositivos() - a.getNegativos())
                .average()
                .orElse(0.0);
    }
}
