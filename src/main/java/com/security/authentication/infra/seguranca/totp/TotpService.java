package com.security.authentication.infra.seguranca.totp;

import com.atlassian.onetime.core.TOTPGenerator;
import com.atlassian.onetime.model.TOTPSecret;
import com.atlassian.onetime.service.RandomSecretProvider;
import com.security.authentication.domain.usuario.Usuario;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class TotpService {

    public String gerarSecret() {
        return new RandomSecretProvider().generateSecret().getBase32Encoded();
    }

    public String gerarQRCode(Usuario usuario) {
        var issuer = "Fórum Hub";

        return String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s",
                issuer, usuario.getUsername(), usuario.getSecret(), issuer
        );
    }

    public Boolean verificarCodigo(String codigo, Usuario logado) {
        var secretDecodificado = TOTPSecret.Companion.fromBase32EncodedString(logado.getSecret());
        var codigoApp = new TOTPGenerator().generateCurrent(secretDecodificado).getValue();

        return codigoApp.equals(codigo);
    }
}
