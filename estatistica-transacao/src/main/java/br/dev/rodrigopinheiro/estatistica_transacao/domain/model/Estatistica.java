package br.dev.rodrigopinheiro.estatistica_transacao.domain.model;

import java.math.BigDecimal;

public record Estatistica(
    long count,
    BigDecimal sum,
    BigDecimal avg,
    BigDecimal min,
    BigDecimal max
) {
    
    public static Estatistica vazia() {
        return new Estatistica(0, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }
}
