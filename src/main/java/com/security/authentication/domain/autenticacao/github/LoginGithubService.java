package com.security.authentication.domain.autenticacao.github;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class LoginGithubService {

    private static final String clientId = "Ov23lin9sYziy9f7rUEI";
    private static final String clientSecret = "0d06623b67e37a69669b103b4206acab3a8325e4";
    private static final String redirectUri = "http://localhost:8080/login/github/autorizado";

    private final RestClient restClient;

    public LoginGithubService(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.build();
    }

    public String gerarUrl() {
        return "https://github.com/login/oauth/authorize"
                + "?client_id=" + clientId
                + "&redirect_uri=" + redirectUri
                + "&scope=read:user,user:email";
    }

    public String obterToken(String code) {
        return restClient.post()
                .uri("https://github.com/login/oauth/access_token")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Map.of("code", code, "client_id", clientId,
                        "client_secret", clientSecret, "redirect_uri", redirectUri)
                )
                .retrieve()
                .body(String.class);
    }
}
