package br.dev.rodrigopinheiro.estatistica_transacao.domain.service;

import java.math.RoundingMode;
import java.util.List;
import org.springframework.stereotype.Component;
import br.dev.rodrigopinheiro.estatistica_transacao.domain.aggregation.BigDecimalStatistics;
import br.dev.rodrigopinheiro.estatistica_transacao.domain.model.Estatistica;
import br.dev.rodrigopinheiro.estatistica_transacao.domain.model.Transacao;

@Component
public class EstatisticaCalculator {

    public Estatistica calcular(List<Transacao> transacoes) {
        if (transacoes == null || transacoes.isEmpty()) {
            return Estatistica.vazia();
        }

        BigDecimalStatistics stats = transacoes.stream()
                .collect(BigDecimalStatistics.summarizing(Transacao::valor));

        return new Estatistica(
                stats.getCount(),
                stats.getSum(),
                stats.getAvg(2, RoundingMode.HALF_UP),
                stats.getMin(),
                stats.getMax());
    }
}