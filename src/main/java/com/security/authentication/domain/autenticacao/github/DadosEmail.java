package com.security.authentication.domain.autenticacao.github;

public record DadosEmail(String email, Boolean primary, Boolean verified, String visibility) {
}
