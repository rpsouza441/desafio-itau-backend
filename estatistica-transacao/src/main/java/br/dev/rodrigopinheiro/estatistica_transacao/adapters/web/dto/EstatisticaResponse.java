package br.dev.rodrigopinheiro.estatistica_transacao.adapters.web.dto;

import java.math.BigDecimal;

public record EstatisticaResponse(
    long count,
    BigDecimal sum,
    BigDecimal avg,
    BigDecimal min,
    BigDecimal max
    ) {
}
