package br.dev.rodrigopinheiro.estatistica_transacao.application.usecase;

import br.dev.rodrigopinheiro.estatistica_transacao.application.port.in.ObterEstatisticasPort;
import br.dev.rodrigopinheiro.estatistica_transacao.domain.aggregation.BigDecimalStatistics;
import br.dev.rodrigopinheiro.estatistica_transacao.domain.model.Estatistica;
import br.dev.rodrigopinheiro.estatistica_transacao.domain.model.Transacao;
import br.dev.rodrigopinheiro.estatistica_transacao.domain.port.out.TransacaoRepository;
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

    public ObterEstatisticasUseCase(TransacaoRepository repository, Relogio relogio) {
        this.repository = repository;
        this.relogio = relogio;
    }

    @Override
    public Estatistica execute(int janelaSegundos) {
        Instant agora = relogio.agora();
        Instant desde = agora.minusSeconds(janelaSegundos);
        
        // Se usando BucketTransacaoRepository, calcula diretamente
        if (repository instanceof BucketTransacaoRepository bucketRepo) {
            return bucketRepo.calcularEstatisticas(desde, agora);
        }
        
        // Caso contrário, usa findSince e calcula manualmente
        List<Transacao> transacoes = repository.findSince(desde);
        BigDecimalStatistics stats = transacoes
                .stream()
                .collect(BigDecimalStatistics.summarizing(Transacao::valor));

        return new Estatistica(
                stats.getCount(),
                stats.getSum(),
                stats.getAvg(2, RoundingMode.HALF_UP),
                stats.getMin(),
                stats.getMax());
    }
}
