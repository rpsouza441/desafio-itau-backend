package br.dev.rodrigopinheiro.estatistica_transacao.infrastructure.repository;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import br.dev.rodrigopinheiro.estatistica_transacao.domain.model.Estatistica;
import br.dev.rodrigopinheiro.estatistica_transacao.domain.model.Transacao;
import br.dev.rodrigopinheiro.estatistica_transacao.domain.port.out.TransacaoRepository;
import br.dev.rodrigopinheiro.estatistica_transacao.infrastructure.bucket.BucketEstatistica;

public class BucketTransacaoRepository implements TransacaoRepository {
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
      // Método mantido para compatibilidade, mas não usado na otimização
        throw new UnsupportedOperationException(
            "Use calcularEstatisticas() para performance O(1)"
        );
    }

    @Override
    public void deleteBefore(Instant limite) {
        long limiteSegundo = limite.getEpochSecond();
        buckets.entrySet().removeIf(entry -> entry.getKey() < limiteSegundo);
    }

    public Estatistica calcularEstatisticas(Instant desde, Instant ate) {
        long desdeSegundo = desde.getEpochSecond();
        long ateSegundo = ate.getEpochSecond();

        BucketEstatistica resultado = new BucketEstatistica();

        // Itera apenas pelos segundos da janela (máximo 60 ou 3600)
        for (long segundo = desdeSegundo; segundo <= ateSegundo; segundo++) {
            BucketEstatistica bucket = buckets.get(segundo);
            if (bucket != null && !bucket.isEmpty()) {
                resultado.combinarCom(bucket);
            }
        }

        return new Estatistica(
                resultado.getCount(),
                resultado.getSum(),
                resultado.getAvg(),
                resultado.getMin(),
                resultado.getMax());
    }

    public int getBucketCount() {
        return buckets.size();
    }

}
