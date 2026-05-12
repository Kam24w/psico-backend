package com.psico.app.ai.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class IAProviderSelector {

    private final List<ProveedorIAAdapter> adaptadores;

    public ProveedorIAAdapter seleccionar(ProveedorIA proveedorPreferido) {
        Optional<ProveedorIAAdapter> adaptador = adaptadores.stream()
                .filter(a -> a.proveedor() == proveedorPreferido)
                .findFirst();
        return adaptador.orElseGet(() -> adaptadores.stream()
                .filter(a -> a.proveedor() == ProveedorIA.GEMINI)
                .findFirst()
                .orElseThrow());
    }
}
