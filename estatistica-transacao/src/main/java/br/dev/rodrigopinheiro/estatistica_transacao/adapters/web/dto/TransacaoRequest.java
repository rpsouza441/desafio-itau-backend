package br.dev.rodrigopinheiro.estatistica_transacao.adapters.web.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados para registro de uma transação financeira")
public record TransacaoRequest(
    @Schema(
        description = "Valor da transação em reais", 
        example = "123.45",
        minimum = "0.01",
        required = true
    )
    BigDecimal valor,
    
    @Schema(
        description = "Data e hora da transação no formato ISO-8601 com timezone", 
        example = "2024-01-15T10:30:00Z",
        required = true
    )
    OffsetDateTime dataHora
) { 
}
