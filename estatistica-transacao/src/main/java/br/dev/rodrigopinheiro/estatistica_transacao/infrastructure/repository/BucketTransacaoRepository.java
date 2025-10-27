package br.dev.rodrigopinheiro.estatistica_transacao.infrastructure.repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.dev.rodrigopinheiro.estatistica_transacao.domain.model.Estatistica;
import br.dev.rodrigopinheiro.estatistica_transacao.domain.model.Transacao;
import br.dev.rodrigopinheiro.estatistica_transacao.domain.port.out.EstatisticaRepository;
import br.dev.rodrigopinheiro.estatistica_transacao.infrastructure.bucket.BucketEstatistica;

public class BucketTransacaoRepository implements EstatisticaRepository {
    
    private static final Logger logger = LoggerFactory.getLogger(BucketTransacaoRepository.class);
    
    private final Map<Long, BucketEstatistica> buckets = new ConcurrentHashMap<>();

    @Override
    public void save(Transacao transacao) {
        long startTime = System.nanoTime();
        long segundo = transacao.dataHora().getEpochSecond();

        buckets.computeIfAbsent(segundo, k -> new BucketEstatistica())
                .addTransacao(transacao.valor());
        
        long duration = System.nanoTime() - startTime;
        logger.debug("Transação salva no bucket {} em {} ns - Total buckets: {}", 
                    segundo, duration, buckets.size());
    }

    @Override
    public void deleteAll() {
        long startTime = System.nanoTime();
        int bucketsRemovidos = buckets.size();
        
        buckets.clear();
        
        long duration = System.nanoTime() - startTime;
        logger.info("Limpeza de buckets concluída em {} ns - {} buckets removidos", 
                   duration, bucketsRemovidos);
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
        long startTime = System.nanoTime();
        long inicioSegundo = since.getEpochSecond();
        long fimSegundo = Instant.now().getEpochSecond();
        long janela = fimSegundo - inicioSegundo + 1;

        BucketEstatistica estatisticaFinal = new BucketEstatistica();
        int bucketsProcessados = 0;

        for (long segundo = inicioSegundo; segundo <= fimSegundo; segundo++) {
            BucketEstatistica bucket = buckets.get(segundo);
            if (bucket != null && !bucket.isEmpty()) {
                estatisticaFinal.combinarCom(bucket);
                bucketsProcessados++;
            }
        }

        Estatistica resultado = new Estatistica(
                estatisticaFinal.getCount(),
                estatisticaFinal.getSum(),
                estatisticaFinal.getAvg(),
                estatisticaFinal.getMin(),
                estatisticaFinal.getMax()
        );
        
        long duration = System.nanoTime() - startTime;
        logger.info("Estatísticas calculadas em {} ns - Janela: {} segundos, Buckets processados: {}/{}, Transações: {}", 
                   duration, janela, bucketsProcessados, buckets.size(), resultado.count());
        
        return resultado;
    }
}
