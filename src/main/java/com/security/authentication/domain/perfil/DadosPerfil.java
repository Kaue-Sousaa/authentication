package com.security.authentication.domain.perfil;

import jakarta.validation.constraints.NotNull;

public record DadosPerfil(@NotNull PerfilNome perfilNome) {
}
