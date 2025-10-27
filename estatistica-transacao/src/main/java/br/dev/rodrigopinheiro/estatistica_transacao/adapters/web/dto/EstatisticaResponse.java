package br.dev.rodrigopinheiro.estatistica_transacao.adapters.web.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Estatísticas calculadas das transações financeiras")
public record EstatisticaResponse(
    @Schema(
        description = "Número total de transações na janela de tempo", 
        example = "5"
    )
    long count,
    
    @Schema(
        description = "Soma total dos valores das transações", 
        example = "1000.50"
    )
    BigDecimal sum,
    
    @Schema(
        description = "Valor médio das transações (arredondado para 2 casas decimais)", 
        example = "200.10"
    )
    BigDecimal avg,
    
    @Schema(
        description = "Menor valor entre as transações", 
        example = "50.00"
    )
    BigDecimal min,
    
    @Schema(
        description = "Maior valor entre as transações", 
        example = "500.00"
    )
    BigDecimal max
    ) {
}
