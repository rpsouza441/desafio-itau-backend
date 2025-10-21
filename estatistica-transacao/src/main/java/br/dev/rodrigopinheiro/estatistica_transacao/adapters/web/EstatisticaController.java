package br.dev.rodrigopinheiro.estatistica_transacao.adapters.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.dev.rodrigopinheiro.estatistica_transacao.adapters.web.dto.EstatisticaResponse;
import br.dev.rodrigopinheiro.estatistica_transacao.adapters.web.mapper.EstatisticaWebMapper;
import br.dev.rodrigopinheiro.estatistica_transacao.application.port.in.ObterEstatisticasPort;
import br.dev.rodrigopinheiro.estatistica_transacao.domain.model.Estatistica;

@RestController
@RequestMapping("/estatistica")
public class EstatisticaController {

    private final ObterEstatisticasPort estatisticaService;
    private final EstatisticaWebMapper mapper;

    public EstatisticaController(ObterEstatisticasPort estatisticaService, 
                                EstatisticaWebMapper mapper) {
        this.estatisticaService = estatisticaService;
        this.mapper = mapper;
    }

    @GetMapping
    public EstatisticaResponse getEstatisticas() {
        Estatistica estatistica = estatisticaService.execute();
        EstatisticaResponse response = mapper.toResponse(estatistica);
        return response;
    }
    
}
