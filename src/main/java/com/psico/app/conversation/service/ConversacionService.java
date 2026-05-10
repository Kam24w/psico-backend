package com.psico.app.conversation.service;

import java.util.Objects;

import org.springframework.lang.NonNull;
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

    public Mensaje procesarMensaje(@NonNull Long usuarioId, String contenido, TipoEmocion emocionActual) {
        Usuario usuario = usuarioService.buscarPorId(usuarioId);

        // Obtener o crear conversación activa y guardar mensaje del usuario (en una transacción)
        Conversacion conversacion = obtenerYGuardarMensajeUsuario(usuario, contenido, emocionActual);

        // Obtener emoción (del request o la última registrada)
        TipoEmocion emocion = emocionActual != null
                ? emocionActual
                : emocionService.obtenerUltimaEmocion(usuarioId);

        // Generar respuesta de IA (FUERA de la transacción para no bloquear conexiones a la BD)
        String respuestaTexto = servicioIA.generarRespuesta(usuarioId, contenido, emocion);

        // Guardar respuesta de IA (en otra transacción)
        return guardarRespuestaIA(conversacion, respuestaTexto, emocion);
    }

    @Transactional
    protected Conversacion obtenerYGuardarMensajeUsuario(Usuario usuario, String contenido, TipoEmocion emocionActual) {
        Conversacion conversacion = conversacionRepository
                .findFirstByUsuarioIdAndActivaTrue(usuario.getId())
                .orElseGet(() -> crearNuevaConversacion(usuario));

        Mensaje mensajeUsuario = Objects.requireNonNull(Mensaje.builder()
                .contenido(contenido)
                .remitente(Mensaje.Remitente.USER)
                .emocionAsociada(emocionActual)
                .conversacion(conversacion)
                .build());
        mensajeRepository.save(mensajeUsuario);

        conversacion.setUpdatedAt(java.time.LocalDateTime.now());
        return conversacionRepository.save(Objects.requireNonNull(conversacion));
    }

    @Transactional
    protected Mensaje guardarRespuestaIA(Conversacion conversacion, String respuestaTexto, TipoEmocion emocion) {
        Mensaje respuestaIA = Objects.requireNonNull(Mensaje.builder()
                .contenido(respuestaTexto)
                .remitente(Mensaje.Remitente.AI)
                .emocionAsociada(emocion)
                .conversacion(conversacion)
                .build());
        return Objects.requireNonNull(mensajeRepository.save(respuestaIA));
    }

    public List<Mensaje> obtenerHistorial(@NonNull Long conversacionId) {
        return mensajeRepository.findByConversacionIdOrderByFechaAsc(conversacionId);
    }

    public List<Conversacion> obtenerConversacionesDeUsuario(@NonNull Long usuarioId) {
        return conversacionRepository.findByUsuarioIdOrderByUpdatedAtDesc(usuarioId);
    }

    private Conversacion crearNuevaConversacion(Usuario usuario) {
        Conversacion nueva = Objects.requireNonNull(Conversacion.builder()
                .usuario(usuario)
                .build());
        return Objects.requireNonNull(conversacionRepository.save(nueva));
    }
}
