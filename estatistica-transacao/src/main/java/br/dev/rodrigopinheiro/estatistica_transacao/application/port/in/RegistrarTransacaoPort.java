package br.dev.rodrigopinheiro.estatistica_transacao.application.port.in;


import br.dev.rodrigopinheiro.estatistica_transacao.domain.model.Transacao;

public interface RegistrarTransacaoPort {
    void execute(Transacao transacao);
}
