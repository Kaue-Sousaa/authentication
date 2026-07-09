package com.security.authentication.domain.autenticacao;

public record DadosToken(String tokenAcesso, String refreshToken, Boolean a2f) {
}
