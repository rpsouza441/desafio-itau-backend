package br.dev.rodrigopinheiro.estatistica_transacao.infrastructure.scheduler;

import java.time.Instant;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.dev.rodrigopinheiro.estatistica_transacao.infrastructure.repository.InMemoryTransacaoRepository;
import br.dev.rodrigopinheiro.estatistica_transacao.infrastructure.time.Relogio;

@Component
public class LimpezaTransacaoAgendada {

    private final InMemoryTransacaoRepository repo;
    private final Relogio relogio;
    private static final long MAX_AGE_SECONDS = 3600;

    public LimpezaTransacaoAgendada(InMemoryTransacaoRepository repo, Relogio relogio) {
        this.repo = repo;
        this.relogio = relogio;
    }

    @Scheduled(fixedRate = 60000) // a cada 1 minuto
    public void limparAntigas() {
        Instant limite = relogio.agora().minusSeconds(MAX_AGE_SECONDS);
        repo.deleteBefore(limite); // implemente este método no repositório
    }
}