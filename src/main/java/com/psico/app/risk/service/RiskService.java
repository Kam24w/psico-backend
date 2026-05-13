package com.psico.app.risk.service;

import com.psico.app.emotion.model.TipoEmocion;
import com.psico.app.risk.model.AlertaRiesgo;
import com.psico.app.risk.model.NivelRiesgo;
import com.psico.app.risk.repository.AlertaRiesgoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RiskService {

    private final AlertaRiesgoRepository alertaRepository;

    public NivelRiesgo evaluarRiesgo(Long usuarioId, TipoEmocion emocion, String mensaje) {
        if (emocion == TipoEmocion.ENOJADO || emocion == TipoEmocion.ESTRESADO) {
            return NivelRiesgo.ALTO;
        }
        if (emocion == TipoEmocion.ANSIOSO || mensaje.contains("no puedo")) {
            return NivelRiesgo.MODERADO;
        }
        return NivelRiesgo.BAJO;
    }

    @Transactional
    public AlertaRiesgo registrarAlerta(Long usuarioId, NivelRiesgo nivel, String razon) {
        AlertaRiesgo alerta = AlertaRiesgo.builder()
                .usuarioId(usuarioId)
                .nivel(nivel)
                .razon(razon)
                .creadoEn(LocalDateTime.now())
                .build();
        return alertaRepository.save(alerta);
    }

    public List<AlertaRiesgo> obtenerAlertas(Long usuarioId) {
        return alertaRepository.findByUsuarioIdOrderByCreadoEnDesc(usuarioId);
    }
}
