package com.security.authentication.domain.autenticacao;

import jakarta.validation.constraints.NotBlank;

public record DadosLogin(@NotBlank String email,
                        @NotBlank String senha) {
}
