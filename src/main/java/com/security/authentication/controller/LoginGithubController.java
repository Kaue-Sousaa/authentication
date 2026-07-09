package com.security.authentication.controller;

import com.security.authentication.domain.autenticacao.DadosToken;
import com.security.authentication.domain.autenticacao.TokenService;
import com.security.authentication.domain.autenticacao.github.LoginGithubService;
import com.security.authentication.domain.usuario.Usuario;
import com.security.authentication.domain.usuario.UsuarioRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/login/github")
public class LoginGithubController {

    private final LoginGithubService loginGithubService;
    private final UsuarioRepository usuarioRepository;
    private final TokenService tokenService;

    public LoginGithubController(LoginGithubService loginGithubService, UsuarioRepository usuarioRepository, TokenService tokenService) {
        this.loginGithubService = loginGithubService;
        this.usuarioRepository = usuarioRepository;
        this.tokenService = tokenService;
    }

    @GetMapping
    public ResponseEntity<Void> redirecionarGithub() {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(loginGithubService.gerarUrl()));

        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    @GetMapping("/autorizado")
    public ResponseEntity<DadosToken> autenticarUsuarioOAuth(@RequestParam String code) {
        String email = loginGithubService.obterEmail(code);

        Usuario usuario = usuarioRepository
                .findByEmailIgnoreCaseAndVerificadoTrue(email).orElseThrow();

        Authentication authentication = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String tokenAcesso = tokenService.gerarToken((Usuario) authentication.getPrincipal());
        String refreshToken = tokenService.gerarRefreshToken((Usuario) authentication.getPrincipal());

        return ResponseEntity.ok(new DadosToken(tokenAcesso, refreshToken, false));
    }
}
