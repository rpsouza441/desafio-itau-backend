package br.dev.rodrigopinheiro.estatistica_transacao.application.usecase;

import br.dev.rodrigopinheiro.estatistica_transacao.application.port.in.ObterEstatisticasPort;
import br.dev.rodrigopinheiro.estatistica_transacao.domain.aggregation.BigDecimalStatistics;
import br.dev.rodrigopinheiro.estatistica_transacao.domain.model.Estatistica;
import br.dev.rodrigopinheiro.estatistica_transacao.domain.model.Transacao;
import br.dev.rodrigopinheiro.estatistica_transacao.domain.port.out.TransacaoRepository;
import br.dev.rodrigopinheiro.estatistica_transacao.domain.service.EstatisticaCalculator;
import br.dev.rodrigopinheiro.estatistica_transacao.infrastructure.config.EstatisticaProperties;
import br.dev.rodrigopinheiro.estatistica_transacao.infrastructure.repository.BucketTransacaoRepository;
import br.dev.rodrigopinheiro.estatistica_transacao.infrastructure.time.Relogio;

import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;

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

        return calculator.calcular(repository.findSince(desde));
    }
}
