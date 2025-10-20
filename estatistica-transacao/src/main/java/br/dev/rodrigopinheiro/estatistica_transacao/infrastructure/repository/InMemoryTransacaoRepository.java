package br.dev.rodrigopinheiro.estatistica_transacao.infrastructure.repository;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.springframework.stereotype.Repository;

import br.dev.rodrigopinheiro.estatistica_transacao.domain.model.Transacao;
import br.dev.rodrigopinheiro.estatistica_transacao.domain.port.out.TransacaoRepository;

@Repository
public class InMemoryTransacaoRepository implements TransacaoRepository {

    private final ConcurrentLinkedDeque<Transacao> transacoes = new ConcurrentLinkedDeque<>();

    @Override
    public void save(Transacao transacao) {
        transacoes.add(transacao);
    }

    @Override
    public void deleteAll() {
        transacoes.clear();
    }

    @Override
    public List<Transacao> findSince(Instant momento) {
        return transacoes.stream().filter(t -> !t.dataHora().isBefore(momento)).toList();
    }

    public void deleteBefore(Instant limite) {
        transacoes.removeIf(t -> t.dataHora().isBefore(limite));
    }

}
