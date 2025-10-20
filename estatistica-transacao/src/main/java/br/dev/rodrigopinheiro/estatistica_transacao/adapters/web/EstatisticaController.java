package br.dev.rodrigopinheiro.estatistica_transacao.adapters.web;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.dev.rodrigopinheiro.estatistica_transacao.adapters.web.dto.EstatisticaResponse;
import br.dev.rodrigopinheiro.estatistica_transacao.adapters.web.mapper.EstatisticaWebMapper;
import br.dev.rodrigopinheiro.estatistica_transacao.application.port.in.ObterEstatisticasPort;
import br.dev.rodrigopinheiro.estatistica_transacao.domain.model.Estatistica;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;

@RestController
@RequestMapping("/estatistica")
@Validated
public class EstatisticaController {

    private final ObterEstatisticasPort estatisticaService;
    private final EstatisticaWebMapper mapper;

    public EstatisticaController(ObterEstatisticasPort estatisticaService, EstatisticaWebMapper mapper) {
        this.estatisticaService = estatisticaService;
        this.mapper = mapper;
    }

    @Validated
    @GetMapping
    public EstatisticaResponse getEstatisticas
            (@RequestParam (required = false)
            @Positive @Max(3600) Long janela) {
        int janelaSegundos = janela != null ? janela.intValue() : 60;
        Estatistica estatistica = estatisticaService.execute(janelaSegundos);
        EstatisticaResponse response = mapper.toResponse(estatistica);
        return response;

    }
    
}
