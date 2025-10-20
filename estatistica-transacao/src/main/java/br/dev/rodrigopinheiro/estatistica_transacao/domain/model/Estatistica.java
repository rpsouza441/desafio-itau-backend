package br.dev.rodrigopinheiro.estatistica_transacao.domain.model;

import java.math.BigDecimal;

public record Estatistica(
    long count,
    BigDecimal sum,
    BigDecimal avg,
    BigDecimal min,
    BigDecimal max
) {
    
}
