package br.dev.rodrigopinheiro.estatistica_transacao.infrastructure.bucket;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import br.dev.rodrigopinheiro.estatistica_transacao.domain.model.Estatistica;
import br.dev.rodrigopinheiro.estatistica_transacao.domain.model.Transacao;
import br.dev.rodrigopinheiro.estatistica_transacao.infrastructure.repository.BucketTransacaoRepository;
import br.dev.rodrigopinheiro.estatistica_transacao.infrastructure.repository.InMemoryTransacaoRepository;

@SpringBootTest
class BucketPerformanceTest {

    private BucketTransacaoRepository bucketRepo;
    private InMemoryTransacaoRepository memoryRepo;

    @BeforeEach
    void setUp() {
        bucketRepo = new BucketTransacaoRepository();
        memoryRepo = new InMemoryTransacaoRepository();
    }


    @Test
    void deveInserirTransacaoComSucesso() {
        // Given
        Transacao transacao = new Transacao(BigDecimal.valueOf(100.50), Instant.now());
        
        // When & Then
        assertDoesNotThrow(() -> bucketRepo.save(transacao));
    }

    @Test
    void deveCalcularEstatisticasCorretamente() {
        // Given
        Instant agora = Instant.now();
        bucketRepo.save(new Transacao(new BigDecimal("100.00"), agora.minusSeconds(10)));
        bucketRepo.save(new Transacao(new BigDecimal("200.00"), agora.minusSeconds(20)));
        bucketRepo.save(new Transacao(new BigDecimal("300.00"), agora.minusSeconds(30)));
        
        // When
        Estatistica stats = bucketRepo.calcularEstatisticas(agora.minusSeconds(60), agora);
        
        // Then
        assertEquals(3, stats.count());
        assertEquals(new BigDecimal("600.00"), stats.sum());
        assertEquals(new BigDecimal("200.00"), stats.avg());
        assertEquals(new BigDecimal("100.00"), stats.min());
        assertEquals(new BigDecimal("300.00"), stats.max());
    }

    @Test
    void deveLimparTransacoesAntigasCorretamente() {
        // Given
        Instant agora = Instant.now();
        bucketRepo.save(new Transacao(new BigDecimal("100.00"), agora.minusSeconds(70))); // Antiga
        bucketRepo.save(new Transacao(new BigDecimal("200.00"), agora.minusSeconds(30))); // Recente
        
        // When
        bucketRepo.deleteBefore(agora.minusSeconds(60));
        Estatistica stats = bucketRepo.calcularEstatisticas(agora.minusSeconds(60), agora);
        
        // Then
        assertEquals(1, stats.count());
        assertEquals(new BigDecimal("200.00"), stats.sum());
    }


    @Test
    void deveRetornarEstatisticasVaziasQuandoNaoHaTransacoes() {
        // When
        Instant agoraLeituraVazio = Instant.now();
        Estatistica stats = bucketRepo.calcularEstatisticas(agoraLeituraVazio.minusSeconds(60), agoraLeituraVazio);
        
        // Then
        assertEquals(0, stats.count());
        assertEquals(BigDecimal.ZERO, stats.sum());
        assertEquals(BigDecimal.ZERO, stats.avg());
        assertEquals(BigDecimal.ZERO, stats.min());
        assertEquals(BigDecimal.ZERO, stats.max());
    }

    @Test
    void deveIgnorarTransacoesComTimestampFuturo() {
        // Given
        Instant agora = Instant.now();
        Instant futuro = agora.plusSeconds(3600); // 1 hora no futuro
        bucketRepo.save(new Transacao(new BigDecimal("100.00"), futuro));
        bucketRepo.save(new Transacao(new BigDecimal("200.00"), agora.minusSeconds(10)));
        
        // When
        Estatistica stats = bucketRepo.calcularEstatisticas(agora.minusSeconds(60), agora);
        
        // Then
        assertEquals(1, stats.count());
        assertEquals(new BigDecimal("200.00"), stats.sum());
    }

    @Test
    void deveIgnorarTransacoesMuitoAntigas() {
        // Given
        Instant agora = Instant.now();
        bucketRepo.save(new Transacao(new BigDecimal("100.00"), agora.minusSeconds(70))); // > 60s
        bucketRepo.save(new Transacao(new BigDecimal("200.00"), agora.minusSeconds(30))); // < 60s
        
        // When
        Estatistica stats = bucketRepo.calcularEstatisticas(agora.minusSeconds(60), agora);
        
        // Then
        assertEquals(1, stats.count());
        assertEquals(new BigDecimal("200.00"), stats.sum());
    }

    @Test
    void deveManterPrecisaoComValoresDecimais() {
        // Given
        Instant agora = Instant.now();
        bucketRepo.save(new Transacao(new BigDecimal("10.33"), agora.minusSeconds(10)));
        bucketRepo.save(new Transacao(new BigDecimal("20.67"), agora.minusSeconds(20)));
        
        // When
        Estatistica stats = bucketRepo.calcularEstatisticas(agora.minusSeconds(60), agora);
        
        // Then
        assertEquals(2, stats.count());
        assertEquals(new BigDecimal("31.00"), stats.sum());
        assertEquals(new BigDecimal("15.50"), stats.avg());
        assertEquals(new BigDecimal("10.33"), stats.min());
        assertEquals(new BigDecimal("20.67"), stats.max());
    }


    @Test
    void compararPerformanceInserção() {
        int numTransacoes = 100_000;
        Instant agora = Instant.now();
        
        // Teste Bucket Repository
        long inicioBucket = System.nanoTime();
        for (int i = 0; i < numTransacoes; i++) {
            Transacao t = new Transacao(
                BigDecimal.valueOf(i), 
                agora.minusSeconds(i % 60)
            );
            bucketRepo.save(t);
        }
        long fimBucket = System.nanoTime();
        
        // Teste Memory Repository
        long inicioMemory = System.nanoTime();
        for (int i = 0; i < numTransacoes; i++) {
            Transacao t = new Transacao(
                BigDecimal.valueOf(i), 
                agora.minusSeconds(i % 60)
            );
            memoryRepo.save(t);
        }
        long fimMemory = System.nanoTime();
        
        long tempoBucket = TimeUnit.NANOSECONDS.toMillis(fimBucket - inicioBucket);
        long tempoMemory = TimeUnit.NANOSECONDS.toMillis(fimMemory - inicioMemory);
        
        System.out.printf("Bucket: %d ms%n", tempoBucket);
        System.out.printf("Memory: %d ms%n", tempoMemory);
        
        // Bucket deve ser comparável ou mais rápido (largura de tolerância)
        assertTrue(tempoBucket <= tempoMemory * 3, 
            "Bucket deveria ser comparável ou até 3x o tempo do Memory em grandes volumes");
    }

    @Test
    void testarPerformanceConsultaEstatisticas() {
        // Given - Inserir dados de teste
        Instant agora = Instant.now();
        for (int i = 0; i < 10_000; i++) {
            bucketRepo.save(new Transacao(
                BigDecimal.valueOf(i), 
                agora.minusSeconds(i % 60)
            ));
            memoryRepo.save(new Transacao(
                BigDecimal.valueOf(i), 
                agora.minusSeconds(i % 60)
            ));
        }
        
        // Teste consulta Bucket
        long inicioBucket = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            bucketRepo.calcularEstatisticas(agora.minusSeconds(60), agora);
        }
        long fimBucket = System.nanoTime();
        
        // Teste consulta Memory
        long inicioMemory = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            var transacoes = memoryRepo.findSince(agora.minusSeconds(60));
            var stats = transacoes.stream()
                .collect(br.dev.rodrigopinheiro.estatistica_transacao.domain.aggregation.BigDecimalStatistics.summarizing(Transacao::valor));
            stats.getSum();
        }
        long fimMemory = System.nanoTime();
        
        long tempoBucket = TimeUnit.NANOSECONDS.toMillis(fimBucket - inicioBucket);
        long tempoMemory = TimeUnit.NANOSECONDS.toMillis(fimMemory - inicioMemory);
        
        System.out.printf("Consulta Bucket: %d ms%n", tempoBucket);
        System.out.printf("Consulta Memory: %d ms%n", tempoMemory);
        
        // Bucket deve ser significativamente mais rápido para consultas
        assertTrue(tempoBucket < tempoMemory, 
            "Bucket deveria ser mais rápido para consultas de estatísticas");
    }

    @Test
    void testarConcorrencia() throws InterruptedException {
        // Given
        int numThreads = 10;
        int transacoesPorThread = 1000;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);
        AtomicInteger erros = new AtomicInteger(0);
        
        // When - Executar inserções concorrentes
        for (int i = 0; i < numThreads; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    Instant agora = Instant.now();
                    for (int j = 0; j < transacoesPorThread; j++) {
                        bucketRepo.save(new Transacao(
                            BigDecimal.valueOf(threadId * 1000 + j),
                            agora.minusSeconds(j % 60)
                        ));
                    }
                } catch (Exception e) {
                    erros.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }
        
        // Then
        assertTrue(latch.await(30, TimeUnit.SECONDS), "Teste de concorrência não completou a tempo");
        assertEquals(0, erros.get(), "Não deveria haver erros durante inserções concorrentes");
        
        // Verificar se todas as transações foram inseridas
        Instant agoraLeitura = Instant.now();
        Estatistica stats = bucketRepo.calcularEstatisticas(agoraLeitura.minusSeconds(60), agoraLeitura);
        assertTrue(stats.count() > 0, "Deveria haver transações após inserções concorrentes");
        
        executor.shutdown();
    }

    @Test
    void testarUsoMemoriaComGrandeVolume() {
        // Given
        Runtime runtime = Runtime.getRuntime();
        long memoriaInicial = runtime.totalMemory() - runtime.freeMemory();
        
        // When - Inserir grande volume de dados
        Instant agora = Instant.now();
        for (int i = 0; i < 500_000; i++) {
            bucketRepo.save(new Transacao(
                BigDecimal.valueOf(i), 
                agora.minusSeconds(i % 60)
            ));
        }
        
        // Then
        runtime.gc(); // Forçar garbage collection
        long memoriaFinal = runtime.totalMemory() - runtime.freeMemory();
        long usoMemoria = memoriaFinal - memoriaInicial;
        
        System.out.printf("Uso de memória: %d MB%n", usoMemoria / (1024 * 1024));
        
        // Verificar que o uso de memória é razoável (menos de 500MB para 500k transações)
        assertTrue(usoMemoria < 500 * 1024 * 1024, 
            "Uso de memória deveria ser menor que 500MB para 500k transações");
    }
}