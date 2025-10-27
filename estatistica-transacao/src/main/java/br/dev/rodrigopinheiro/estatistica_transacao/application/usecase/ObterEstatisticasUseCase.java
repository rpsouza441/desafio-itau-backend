package br.dev.rodrigopinheiro.estatistica_transacao.application.usecase;

import br.dev.rodrigopinheiro.estatistica_transacao.application.port.in.ObterEstatisticasPort;
import br.dev.rodrigopinheiro.estatistica_transacao.domain.model.Estatistica;
import br.dev.rodrigopinheiro.estatistica_transacao.domain.port.out.TransacaoRepository;
import br.dev.rodrigopinheiro.estatistica_transacao.domain.port.out.EstatisticaRepository;
import br.dev.rodrigopinheiro.estatistica_transacao.domain.service.EstatisticaCalculator;
import br.dev.rodrigopinheiro.estatistica_transacao.infrastructure.config.EstatisticaProperties;
import br.dev.rodrigopinheiro.estatistica_transacao.infrastructure.time.Relogio;

import java.time.Instant;

import org.springframework.stereotype.Service;

@Service
public class ObterEstatisticasUseCase implements ObterEstatisticasPort {
    private final TransacaoRepository repository;
    private final Relogio relogio;
    private final EstatisticaProperties estatisticaProperties;
    private final EstatisticaCalculator calculator;

    public ObterEstatisticasUseCase(TransacaoRepository repository, Relogio relogio,
            EstatisticaProperties estatisticaProperties, EstatisticaCalculator calculator) {
        this.repository = repository;
        this.relogio = relogio;
        this.estatisticaProperties = estatisticaProperties;
        this.calculator = calculator;
    }

    @Override
    public Estatistica execute() {
        Instant agora = relogio.agora();
        Instant desde = agora.minusSeconds(estatisticaProperties.getJanelaSegundos());

        // Strategy Pattern: usa método otimizado se disponível, senão usa abordagem tradicional
        if (repository instanceof EstatisticaRepository estatisticaRepo) {
            // Estratégia otimizada: calcula diretamente dos buckets (O(1))
            return estatisticaRepo.calcularEstatisticasSince(desde);
        } else {
            // Estratégia tradicional: busca transações e calcula (O(n))
            return calculator.calcular(repository.findSince(desde));
        }
    }
}
