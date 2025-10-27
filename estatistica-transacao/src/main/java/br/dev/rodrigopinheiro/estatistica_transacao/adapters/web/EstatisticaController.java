package br.dev.rodrigopinheiro.estatistica_transacao.adapters.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.dev.rodrigopinheiro.estatistica_transacao.adapters.web.dto.EstatisticaResponse;
import br.dev.rodrigopinheiro.estatistica_transacao.adapters.web.mapper.EstatisticaWebMapper;
import br.dev.rodrigopinheiro.estatistica_transacao.application.port.in.ObterEstatisticasPort;
import br.dev.rodrigopinheiro.estatistica_transacao.domain.model.Estatistica;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/estatistica")
@Tag(name = "Estatísticas", description = "Operações para consulta de estatísticas das transações financeiras")
public class EstatisticaController {

    private static final Logger logger = LoggerFactory.getLogger(EstatisticaController.class);

    private final ObterEstatisticasPort obterEstatisticasPort;
    private final EstatisticaWebMapper estatisticaWebMapper;

    public EstatisticaController(ObterEstatisticasPort obterEstatisticasPort, 
                                 EstatisticaWebMapper estatisticaWebMapper) {
        this.obterEstatisticasPort = obterEstatisticasPort;
        this.estatisticaWebMapper = estatisticaWebMapper;
    }

    @Operation(
        summary = "Obtém estatísticas das transações",
        description = "Retorna estatísticas calculadas das transações registradas nos últimos 60 segundos " +
                     "(janela configurável). Inclui contagem, soma, média, valor mínimo e máximo."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Estatísticas calculadas com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = EstatisticaResponse.class)
            )
        )
    })
    @GetMapping
    public EstatisticaResponse getEstatisticas() {
        logger.info("Iniciando consulta de estatísticas das transações");
        
        Estatistica estatistica = obterEstatisticasPort.execute();
        EstatisticaResponse response = estatisticaWebMapper.toResponse(estatistica);
        
        logger.info("Estatísticas consultadas com sucesso - Count: {}, Sum: {}, Avg: {}, Min: {}, Max: {}", 
                   response.count(), response.sum(), response.avg(), response.min(), response.max());
        
        return response;
    }
    
}
