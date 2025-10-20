package br.dev.rodrigopinheiro.estatistica_transacao.domain.model;

import java.math.BigDecimal;
import java.time.Instant;

public record Transacao
(
    BigDecimal valor, 
    Instant dataHora
) {
} 


