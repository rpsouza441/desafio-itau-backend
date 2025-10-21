package br.dev.rodrigopinheiro.estatistica_transacao.application.port.in;

import br.dev.rodrigopinheiro.estatistica_transacao.domain.model.Estatistica;

public interface ObterEstatisticasPort {
    Estatistica execute();
}
