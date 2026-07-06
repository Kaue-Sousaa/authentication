package com.security.authentication.domain.topico;

import com.security.authentication.domain.resposta.DadosListagemResposta;
import com.security.authentication.domain.resposta.Resposta;

import java.util.List;

public record DadosDetalhesTopico(DadosListagemTopico dadosListagem, List<DadosListagemResposta> respostas) {
    public DadosDetalhesTopico(Topico topico, List<Resposta> respostas) {
        this(new DadosListagemTopico(topico), respostas.stream().map(DadosListagemResposta::new).toList());
    }
}