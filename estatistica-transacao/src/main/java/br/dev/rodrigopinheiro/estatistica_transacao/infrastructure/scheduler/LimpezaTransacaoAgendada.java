package br.dev.rodrigopinheiro.estatistica_transacao.infrastructure.scheduler;

import java.time.Instant;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.dev.rodrigopinheiro.estatistica_transacao.infrastructure.config.LimpezaProperties;
import br.dev.rodrigopinheiro.estatistica_transacao.infrastructure.repository.InMemoryTransacaoRepository;
import br.dev.rodrigopinheiro.estatistica_transacao.infrastructure.time.Relogio;

@Component
public class LimpezaTransacaoAgendada {

    private final InMemoryTransacaoRepository repo;
    private final Relogio relogio;
    private final LimpezaProperties limpezaProperties;

    public LimpezaTransacaoAgendada(InMemoryTransacaoRepository repo, 
                                   Relogio relogio,
                                   LimpezaProperties limpezaProperties) {
        this.repo = repo;
        this.relogio = relogio;
        this.limpezaProperties = limpezaProperties;
    }

    @Scheduled(fixedRateString = "#{@limpezaProperties.intervaloMilissegundos}")
    public void limparAntigas() {
        Instant limite = relogio.agora().minusSeconds(limpezaProperties.getMaxAgeSegundos());
        repo.deleteBefore(limite);
    }
}