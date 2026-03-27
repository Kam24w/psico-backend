package com.psico.app.conversation.service;

import com.psico.app.ai.service.ServicioIA;
import com.psico.app.auth.model.Usuario;
import com.psico.app.conversation.model.Conversacion;
import com.psico.app.conversation.model.Mensaje;
import com.psico.app.conversation.repository.ConversacionRepository;
import com.psico.app.conversation.repository.MensajeRepository;
import com.psico.app.emotion.model.TipoEmocion;
import com.psico.app.emotion.service.EmocionService;
import com.psico.app.user.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConversacionService {

    private final ConversacionRepository conversacionRepository;
    private final MensajeRepository mensajeRepository;
    private final ServicioIA servicioIA;
    private final EmocionService emocionService;
    private final UsuarioService usuarioService;

    @Transactional
    public Mensaje procesarMensaje(Long usuarioId, String contenido, TipoEmocion emocionActual) {
        Usuario usuario = usuarioService.buscarPorId(usuarioId);

        // Obtener o crear conversación activa
        Conversacion conversacion = conversacionRepository
                .findFirstByUsuarioIdAndActivaTrue(usuarioId)
                .orElseGet(() -> crearNuevaConversacion(usuario));

        // Guardar mensaje del usuario
        Mensaje mensajeUsuario = Mensaje.builder()
                .contenido(contenido)
                .remitente(Mensaje.Remitente.USER)
                .emocionAsociada(emocionActual)
                .conversacion(conversacion)
                .build();
        mensajeRepository.save(mensajeUsuario);

        // Obtener emoción (del request o la última registrada)
        TipoEmocion emocion = emocionActual != null
                ? emocionActual
                : emocionService.obtenerUltimaEmocion(usuarioId);

        // Generar respuesta de IA
        String respuestaTexto = servicioIA.generarRespuesta(contenido, emocion);

        // Guardar respuesta de IA
        Mensaje respuestaIA = Mensaje.builder()
                .contenido(respuestaTexto)
                .remitente(Mensaje.Remitente.AI)
                .emocionAsociada(emocion)
                .conversacion(conversacion)
                .build();
        mensajeRepository.save(respuestaIA);

        log.info("Mensaje procesado para usuario {} con emoción {}", usuarioId, emocion);
        return respuestaIA;
    }

    public List<Mensaje> obtenerHistorial(Long conversacionId) {
        return mensajeRepository.findByConversacionIdOrderByFechaAsc(conversacionId);
    }

    public List<Conversacion> obtenerConversacionesDeUsuario(Long usuarioId) {
        return conversacionRepository.findByUsuarioIdOrderByUpdatedAtDesc(usuarioId);
    }

    private Conversacion crearNuevaConversacion(Usuario usuario) {
        Conversacion nueva = Conversacion.builder()
                .usuario(usuario)
                .build();
        return conversacionRepository.save(nueva);
    }
}
