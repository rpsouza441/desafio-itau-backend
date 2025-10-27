package br.dev.rodrigopinheiro.estatistica_transacao.infrastructure.repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import br.dev.rodrigopinheiro.estatistica_transacao.domain.model.Estatistica;
import br.dev.rodrigopinheiro.estatistica_transacao.domain.model.Transacao;
import br.dev.rodrigopinheiro.estatistica_transacao.domain.port.out.EstatisticaRepository;
import br.dev.rodrigopinheiro.estatistica_transacao.infrastructure.bucket.BucketEstatistica;

public class BucketTransacaoRepository implements EstatisticaRepository {
    private final Map<Long, BucketEstatistica> buckets = new ConcurrentHashMap<>();

    @Override
    public void save(Transacao transacao) {
        long segundo = transacao.dataHora().getEpochSecond();

        buckets.computeIfAbsent(segundo, k -> new BucketEstatistica())
                .addTransacao(transacao.valor());
    }

    @Override
    public void deleteAll() {
        buckets.clear();
    }

    @Override
    public List<Transacao> findSince(Instant since) {
        List<Transacao> transacoes = new ArrayList<>();
        long inicioSegundo = since.getEpochSecond();
        long fimSegundo = Instant.now().getEpochSecond();

        for (long segundo = inicioSegundo; segundo <= fimSegundo; segundo++) {
            BucketEstatistica bucket = buckets.get(segundo);
            if (bucket != null && !bucket.isEmpty()) {
                // Reconstrói transações aproximadas usando valor médio
                for (long i = 0; i < bucket.getCount(); i++) {
                    transacoes.add(new Transacao(
                            bucket.getAvg(),
                            Instant.ofEpochSecond(segundo)));
                }
            }
        }

        return transacoes;
    }

    @Override
    public void deleteBefore(Instant limite) {
        long limiteSegundo = limite.getEpochSecond();
        buckets.entrySet().removeIf(entry -> entry.getKey() < limiteSegundo);
    }

    public int getBucketCount() {
        return buckets.size();
    }

    /**
     * Método otimizado que calcula estatísticas diretamente dos buckets
     * sem reconstruir objetos Transacao - muito mais rápido!
     */
    public Estatistica calcularEstatisticasSince(Instant since) {
        long inicioSegundo = since.getEpochSecond();
        long fimSegundo = Instant.now().getEpochSecond();

        BucketEstatistica estatisticaFinal = new BucketEstatistica();

        for (long segundo = inicioSegundo; segundo <= fimSegundo; segundo++) {
            BucketEstatistica bucket = buckets.get(segundo);
            if (bucket != null && !bucket.isEmpty()) {
                estatisticaFinal.combinarCom(bucket);
            }
        }

        return new Estatistica(
                estatisticaFinal.getCount(),
                estatisticaFinal.getSum(),
                estatisticaFinal.getAvg(),
                estatisticaFinal.getMin(),
                estatisticaFinal.getMax()
        );
    }
}
