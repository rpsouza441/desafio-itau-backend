package br.dev.rodrigopinheiro.estatistica_transacao.adapters.web.mapper;

import org.springframework.stereotype.Component;

import br.dev.rodrigopinheiro.estatistica_transacao.adapters.web.dto.EstatisticaResponse;
import br.dev.rodrigopinheiro.estatistica_transacao.domain.model.Estatistica;

@Component
public class EstatisticaWebMapper {

    public EstatisticaResponse toResponse(Estatistica estatistica) {
        return new EstatisticaResponse(
            estatistica.count(),
           estatistica.sum(),
           estatistica.avg(),
           estatistica.min(),
           estatistica.max()
        );
    }
    
}
