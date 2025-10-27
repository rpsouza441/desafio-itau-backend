package br.dev.rodrigopinheiro.estatistica_transacao.infrastructure.bucket;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import br.dev.rodrigopinheiro.estatistica_transacao.domain.model.Estatistica;
import br.dev.rodrigopinheiro.estatistica_transacao.domain.model.Transacao;
import br.dev.rodrigopinheiro.estatistica_transacao.domain.service.EstatisticaCalculator;
import br.dev.rodrigopinheiro.estatistica_transacao.infrastructure.repository.BucketTransacaoRepository;
import br.dev.rodrigopinheiro.estatistica_transacao.infrastructure.repository.InMemoryTransacaoRepository;

@SpringBootTest
class BucketPerformanceTest {

    private BucketTransacaoRepository bucketRepo;
    private InMemoryTransacaoRepository memoryRepo;
    private EstatisticaCalculator calculator;

    @BeforeEach
    void setUp() {
        bucketRepo = new BucketTransacaoRepository();
        memoryRepo = new InMemoryTransacaoRepository();
        calculator = new EstatisticaCalculator();
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

        bucketRepo.save(new Transacao(new BigDecimal("100.00"), agora.minusSeconds(30)));
        bucketRepo.save(new Transacao(new BigDecimal("200.00"), agora.minusSeconds(20)));

        // When
        List<Transacao> transacoes = bucketRepo.findSince(agora.minusSeconds(60));
        Estatistica stats = calculator.calcular(transacoes);

        // Then
        assertEquals(2, stats.count());
        assertEquals(new BigDecimal("300.00"), stats.sum());
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
                    agora.minusSeconds(i % 60));
            bucketRepo.save(t);
        }
        long fimBucket = System.nanoTime();

        // Teste Memory Repository
        long inicioMemory = System.nanoTime();
        for (int i = 0; i < numTransacoes; i++) {
            Transacao t = new Transacao(
                    BigDecimal.valueOf(i),
                    agora.minusSeconds(i % 60));
            memoryRepo.save(t);
        }
        long fimMemory = System.nanoTime();

        long tempoBucket = TimeUnit.NANOSECONDS.toMillis(fimBucket - inicioBucket);
        long tempoMemory = TimeUnit.NANOSECONDS.toMillis(fimMemory - inicioMemory);

        System.out.printf("Bucket: %d ms%n", tempoBucket);
        System.out.printf("Memory: %d ms%n", tempoMemory);

        // Bucket pode ser mais lento devido à agregação, mas deve ser razoável
        assertTrue(tempoBucket <= tempoMemory * 10,
                "Bucket deveria ser comparável ou até 10x o tempo do Memory em grandes volumes");
    }

    @Test
    void testarPerformanceConsultaEstatisticas() {
        // Given - Inserir dados de teste
        Instant agora = Instant.now();
        for (int i = 0; i < 10_000; i++) {
            bucketRepo.save(new Transacao(
                    BigDecimal.valueOf(i),
                    agora.minusSeconds(i % 60)));
            memoryRepo.save(new Transacao(
                    BigDecimal.valueOf(i),
                    agora.minusSeconds(i % 60)));
        }

        // Teste consulta Bucket - Nova arquitetura SOLID
        long inicioBucket = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            List<Transacao> transacoes = bucketRepo.findSince(agora.minusSeconds(60));
            calculator.calcular(transacoes);
        }
        long fimBucket = System.nanoTime();

        // Teste consulta Memory - Nova arquitetura SOLID
        long inicioMemory = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            List<Transacao> transacoes = memoryRepo.findSince(agora.minusSeconds(60));
            calculator.calcular(transacoes);
        }
        long fimMemory = System.nanoTime();

        long tempoBucket = TimeUnit.NANOSECONDS.toMillis(fimBucket - inicioBucket);
        long tempoMemory = TimeUnit.NANOSECONDS.toMillis(fimMemory - inicioMemory);

        System.out.printf("Consulta Bucket: %d ms%n", tempoBucket);
        System.out.printf("Consulta Memory: %d ms%n", tempoMemory);

        // Com a nova arquitetura SOLID, ambos usam findSince() + calculator.calcular()
        // Bucket pode ser comparável ou ligeiramente mais lento devido à reconstrução de objetos
        assertTrue(tempoBucket <= tempoMemory * 2,
                "Bucket deveria ser comparável ao Memory (até 2x mais lento) na nova arquitetura SOLID");
    }

    @Test
    void testarPerformanceBucketOtimizado() {
        // Given - Inserir MUITOS dados de teste
        Instant agora = Instant.now();
        for (int i = 0; i < 100_000; i++) {
            bucketRepo.save(new Transacao(
                    BigDecimal.valueOf(i),
                    agora.minusSeconds(i % 60)));
            memoryRepo.save(new Transacao(
                    BigDecimal.valueOf(i),
                    agora.minusSeconds(i % 60)));
        }

        // Teste consulta Bucket OTIMIZADA - Agrega diretamente dos buckets
        long inicioBucketOtimizado = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            bucketRepo.calcularEstatisticasSince(agora.minusSeconds(60));
        }
        long fimBucketOtimizado = System.nanoTime();

        // Teste consulta Memory - Arquitetura SOLID tradicional
        long inicioMemory = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            List<Transacao> transacoes = memoryRepo.findSince(agora.minusSeconds(60));
            calculator.calcular(transacoes);
        }
        long fimMemory = System.nanoTime();

        long tempoBucketOtimizado = TimeUnit.NANOSECONDS.toMillis(fimBucketOtimizado - inicioBucketOtimizado);
        long tempoMemory = TimeUnit.NANOSECONDS.toMillis(fimMemory - inicioMemory);

        System.out.printf("Bucket OTIMIZADO: %d ms%n", tempoBucketOtimizado);
        System.out.printf("Memory tradicional: %d ms%n", tempoMemory);

        // AGORA SIM! Bucket otimizado deveria ser MUITO mais rápido
        assertTrue(tempoBucketOtimizado < tempoMemory,
                "Bucket OTIMIZADO deveria ser mais rápido que Memory - O(60) vs O(n)");
        
        // Deve ser pelo menos 2x mais rápido
        assertTrue(tempoBucketOtimizado * 2 < tempoMemory,
                "Bucket OTIMIZADO deveria ser pelo menos 2x mais rápido que Memory");
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
                    agora.minusSeconds(i % 60)));
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