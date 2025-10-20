package br.dev.rodrigopinheiro.estatistica_transacao.domain.aggregation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class BigDecimalStatisticsTest {

    @Test
    @DisplayName("Deve inicializar com valores zerados")
    void shouldInitializeWithZeroValues() {
        BigDecimalStatistics stats = new BigDecimalStatistics();

        assertEquals(0, stats.getCount());
        assertEquals(BigDecimal.ZERO, stats.getSum());
        assertEquals(BigDecimal.ZERO, stats.getMin());
        assertEquals(BigDecimal.ZERO, stats.getMax());
        assertEquals(BigDecimal.ZERO.setScale(2), stats.getAvg(2, RoundingMode.HALF_UP));
    }

    @Test
    @DisplayName("Deve aceitar valores únicos corretamente")
    void shouldAcceptSingleValueCorrectly() {
        BigDecimalStatistics stats = new BigDecimalStatistics();
        BigDecimal value = new BigDecimal("100.50");

        stats.accept(value);

        assertEquals(1, stats.getCount());
        assertEquals(value, stats.getSum());
        assertEquals(value, stats.getMin());
        assertEquals(value, stats.getMax());
        assertEquals(value.setScale(2), stats.getAvg(2, RoundingMode.HALF_UP));
    }

    @Test
    @DisplayName("Deve aceitar múltiplos valores corretamente")
    void shouldAcceptMultipleValuesCorrectly() {
        BigDecimalStatistics stats = new BigDecimalStatistics();

        stats.accept(new BigDecimal("100.00"));
        stats.accept(new BigDecimal("200.00"));
        stats.accept(new BigDecimal("50.00"));

        assertEquals(3, stats.getCount());
        assertEquals(new BigDecimal("350.00"), stats.getSum());
        assertEquals(new BigDecimal("50.00"), stats.getMin());
        assertEquals(new BigDecimal("200.00"), stats.getMax());
        assertEquals(new BigDecimal("116.67"), stats.getAvg(2, RoundingMode.HALF_UP));
    }

    @Test
    @DisplayName("Deve ignorar valores nulos")
    void shouldIgnoreNullValues() {
        BigDecimalStatistics stats = new BigDecimalStatistics();

        stats.accept(new BigDecimal("100.00"));
        stats.accept(null);
        stats.accept(new BigDecimal("200.00"));

        assertEquals(2, stats.getCount());
        assertEquals(new BigDecimal("300.00"), stats.getSum());
    }

    @Test
    @DisplayName("Deve combinar estatísticas corretamente")
    void shouldCombineStatisticsCorrectly() {
        BigDecimalStatistics stats1 = new BigDecimalStatistics();
        stats1.accept(new BigDecimal("100.00"));
        stats1.accept(new BigDecimal("200.00"));

        BigDecimalStatistics stats2 = new BigDecimalStatistics();
        stats2.accept(new BigDecimal("50.00"));
        stats2.accept(new BigDecimal("300.00"));

        stats1.combine(stats2);

        assertEquals(4, stats1.getCount());
        assertEquals(new BigDecimal("650.00"), stats1.getSum());
        assertEquals(new BigDecimal("50.00"), stats1.getMin());
        assertEquals(new BigDecimal("300.00"), stats1.getMax());
    }

    @Test
    @DisplayName("Deve lidar com combine de estatística vazia")
    void shouldHandleCombineWithEmptyStatistics() {
        BigDecimalStatistics stats1 = new BigDecimalStatistics();
        stats1.accept(new BigDecimal("100.00"));

        BigDecimalStatistics emptyStats = new BigDecimalStatistics();

        stats1.combine(emptyStats);

        assertEquals(1, stats1.getCount());
        assertEquals(new BigDecimal("100.00"), stats1.getSum());
    }

    @Test
    @DisplayName("Deve funcionar como collector para BigDecimal")
    void shouldWorkAsCollectorForBigDecimal() {
        List<BigDecimal> values = Arrays.asList(
                new BigDecimal("100.00"),
                new BigDecimal("200.00"),
                new BigDecimal("50.00"));

        BigDecimalStatistics result = values.stream()
                .collect(BigDecimalStatistics.toCollector());

        assertEquals(3, result.getCount());
        assertEquals(new BigDecimal("350.00"), result.getSum());
        assertEquals(new BigDecimal("50.00"), result.getMin());
        assertEquals(new BigDecimal("200.00"), result.getMax());
    }

    @Test
    @DisplayName("Deve funcionar como collector com mapper")
    void shouldWorkAsCollectorWithMapper() {
        List<String> stringValues = Arrays.asList("100.00", "200.00", "50.00");

        BigDecimalStatistics result = stringValues.stream()
                .collect(BigDecimalStatistics.summarizing(s -> new BigDecimal(s)));

        assertEquals(3, result.getCount());
        assertEquals(new BigDecimal("350.00"), result.getSum());
    }

    @Test
    @DisplayName("Deve lidar com stream vazio")
    void shouldHandleEmptyStream() {
        List<BigDecimal> emptyList = Collections.emptyList();

        BigDecimalStatistics result = emptyList.stream()
                .collect(BigDecimalStatistics.toCollector());

        assertEquals(0, result.getCount());
        assertEquals(BigDecimal.ZERO, result.getSum());
    }

    @Test
    @DisplayName("Deve calcular média com diferentes escalas e arredondamentos")
    void shouldCalculateAverageWithDifferentScalesAndRounding() {
        BigDecimalStatistics stats = new BigDecimalStatistics();
        stats.accept(new BigDecimal("10.00"));
        stats.accept(new BigDecimal("20.00"));
        stats.accept(new BigDecimal("30.00"));
        // Soma = 60, Count = 3, Média = 20.00

        assertEquals(new BigDecimal("20.00"), stats.getAvg(2, RoundingMode.HALF_UP));
        assertEquals(new BigDecimal("20.0"), stats.getAvg(1, RoundingMode.HALF_UP));

        // Teste com divisão que gera dízima
        stats.accept(new BigDecimal("1.00")); // Agora soma = 61, count = 4
        assertEquals(new BigDecimal("15.25"), stats.getAvg(2, RoundingMode.HALF_UP));
    }

    @Test
    @DisplayName("Deve lidar com valores muito grandes")
    void shouldHandleVeryLargeValues() {
        BigDecimalStatistics stats = new BigDecimalStatistics();
        BigDecimal largeValue = new BigDecimal("999999999999.99");

        stats.accept(largeValue);

        assertEquals(largeValue, stats.getSum());
        assertEquals(largeValue, stats.getMin());
        assertEquals(largeValue, stats.getMax());
    }
}
