package com.security.authentication.domain.autenticacao;

import jakarta.validation.constraints.NotBlank;

public record DadosA2f(@NotBlank String email, @NotBlank String codigo) {
}
