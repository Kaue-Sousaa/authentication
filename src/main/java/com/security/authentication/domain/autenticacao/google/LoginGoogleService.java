package com.security.authentication.domain.autenticacao.google;

import com.auth0.jwt.JWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;
import java.util.Objects;

@Service
public class LoginGoogleService {

    @Value("${google.oauth.clientId}")
    private String clientId;

    @Value("${google.oauth.clientSecret}")
    private String clientSecret;

    private static final String redirectUri = "http://localhost:8080/login/google/autorizado";

    private final RestClient restClient;

    public LoginGoogleService(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.build();
    }

    public String gerarUrl(){
        return "https://accounts.google.com/o/oauth2/v2/auth"+
                "?client_id="+clientId +
                "&redirect_uri="+redirectUri +
                "&scope=https://www.googleapis.com/auth/userinfo.email" +
                "&response_type=code";
    }

    private String obterToken(String code) {
        return Objects.requireNonNull(restClient.post()
                .uri("https://oauth2.googleapis.com/token")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Map.of("code", code, "client_id", clientId,
                        "client_secret", clientSecret, "redirect_uri", redirectUri,
                        "grant_type", "authorization_code")
                )
                .retrieve()
                .body(Map.class)).get("id_token").toString();
    }

    public String obterEmail(String code){
        String token = obterToken(code);
        return JWT.decode(token).getClaim("email").asString();
    }
}
