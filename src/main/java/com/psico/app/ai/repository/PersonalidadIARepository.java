package com.psico.app.ai.repository;

import com.psico.app.ai.model.PersonalidadIA;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PersonalidadIARepository extends JpaRepository<PersonalidadIA, Long> {
    Optional<PersonalidadIA> findByActivaTrue();
}
