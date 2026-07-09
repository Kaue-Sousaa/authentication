package com.security.authentication.controller;

import com.security.authentication.domain.autenticacao.*;
import com.security.authentication.domain.usuario.Usuario;
import com.security.authentication.domain.usuario.UsuarioRepository;
import com.security.authentication.infra.seguranca.totp.TotpService;
import jakarta.validation.Valid;
import org.aspectj.weaver.patterns.ThisOrTargetPointcut;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AutenticacaoController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UsuarioRepository usuarioRepository;
    private final TotpService totpService;

    public AutenticacaoController(AuthenticationManager authenticationManager, TokenService tokenService, UsuarioRepository usuarioRepository, TotpService totpService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.usuarioRepository = usuarioRepository;
        this.totpService = totpService;
    }

    @PostMapping("/login")
    public ResponseEntity<DadosToken> efetuarLogin(@Valid @RequestBody DadosLogin dados){
        var autenticationToken = new UsernamePasswordAuthenticationToken(dados.email(), dados.senha());
        var authentication = authenticationManager.authenticate(autenticationToken);

        var usuario = (Usuario) authentication.getPrincipal();
        if(usuario.isA2fAtiva()) {
            return ResponseEntity.ok(new DadosToken(null, null, true));
        }

        String tokenAcesso = tokenService.gerarToken(usuario);
        String refreshToken = tokenService.gerarRefreshToken(usuario);

        return ResponseEntity.ok(new DadosToken(tokenAcesso, refreshToken, false));
    }

    @PostMapping("/verificar-a2f")
    public ResponseEntity<DadosToken> verificarA2f(@Valid @RequestBody DadosA2f dadosA2f) {
        var usuario = usuarioRepository.findByEmailIgnoreCaseAndVerificadoTrue(dadosA2f.email()).orElseThrow();
        var codigoValido = totpService.verificarCodigo(dadosA2f.codigo(), usuario);

        if (!codigoValido) {
            throw new BadCredentialsException("Código inválido!");
        }

        String tokenAcesso = tokenService.gerarToken(usuario);
        String refreshToken = tokenService.gerarRefreshToken(usuario);

        return ResponseEntity.ok(new DadosToken(tokenAcesso, refreshToken, false));
    }

    @PostMapping("/atualizar-token")
    public ResponseEntity<DadosToken> atualizarToken(@Valid @RequestBody DadosRefreshToken dados){
        var refreshToken = dados.refreshToken();
        Long idUsuario = Long.valueOf(tokenService.verificarToken(refreshToken));
        var usuario = usuarioRepository.findById(idUsuario).orElseThrow();

        String tokenAcesso = tokenService.gerarToken(usuario);
        String tokenAtualizacao = tokenService.gerarRefreshToken(usuario);

        return ResponseEntity.ok(new DadosToken(tokenAcesso, tokenAtualizacao, false));
    }
}
