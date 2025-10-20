package br.dev.rodrigopinheiro.estatistica_transacao.adapters.web.mapper;

import org.springframework.stereotype.Component;

import br.dev.rodrigopinheiro.estatistica_transacao.adapters.web.dto.TransacaoRequest;
import br.dev.rodrigopinheiro.estatistica_transacao.domain.model.Transacao;

@Component
public class TransacaoWebMapper {
    
    public Transacao toDomain(TransacaoRequest request) {
        return new Transacao(
            request.valor(),
            request.dataHora() != null ? request.dataHora().toInstant() : null
        );
    }
}
