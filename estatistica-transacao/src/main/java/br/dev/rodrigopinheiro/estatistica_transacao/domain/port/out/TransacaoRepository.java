package br.dev.rodrigopinheiro.estatistica_transacao.domain.port.out;

import java.time.Instant;
import java.util.List;

import br.dev.rodrigopinheiro.estatistica_transacao.domain.model.Transacao;

public interface TransacaoRepository {
    void save(Transacao transacao);
    void deleteAll();
    List<Transacao> findSince(Instant since);
    void deleteBefore(Instant limite);
}
