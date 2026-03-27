package com.psico.app.emotion.model;

import com.psico.app.auth.model.Usuario;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "historial_emocional")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistorialEmocional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "historial_id")
    @Builder.Default
    private List<Emocion> emociones = new ArrayList<>();

    public void agregarEmocion(Emocion emocion) {
        this.emociones.add(emocion);
    }

    public TipoEmocion getUltimaEmocion() {
        if (emociones.isEmpty()) return TipoEmocion.NEUTRAL;
        return emociones.get(emociones.size() - 1).getTipo();
    }
}
