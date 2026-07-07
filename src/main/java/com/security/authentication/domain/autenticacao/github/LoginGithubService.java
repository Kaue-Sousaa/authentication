package com.security.authentication.domain.autenticacao.github;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;
import java.util.Objects;

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

    private String obterToken(String code) {
        return Objects.requireNonNull(restClient.post()
                .uri("https://github.com/login/oauth/access_token")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Map.of("code", code, "client_id", clientId,
                        "client_secret", clientSecret, "redirect_uri", redirectUri)
                )
                .retrieve()
                .body(Map.class)).get("access_token").toString();
    }

    public String obterEmail(String code){
        var token = obterToken(code);

        var headers = new HttpHeaders();
        headers.setBearerAuth(token);

        var resposta = restClient.get()
                .uri("https://api.github.com/user/emails")
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(DadosEmail[].class);

        var repositorios = restClient.get()
                .uri("https://api.github.com/user/repos")
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(String.class);

        System.out.println(repositorios);

        for (DadosEmail dadosEmail : resposta){
            if (dadosEmail.primary() && dadosEmail.verified()) {
                return dadosEmail.email();
            }
        }

        return null;
    }
}
