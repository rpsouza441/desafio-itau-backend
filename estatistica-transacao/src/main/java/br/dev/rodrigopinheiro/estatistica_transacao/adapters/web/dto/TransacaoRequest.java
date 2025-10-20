package br.dev.rodrigopinheiro.estatistica_transacao.adapters.web.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record TransacaoRequest(
    BigDecimal valor,
    OffsetDateTime dataHora
) { 
}
