package br.dev.rodrigopinheiro.estatistica_transacao.adapters.web;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import br.dev.rodrigopinheiro.estatistica_transacao.adapters.web.dto.TransacaoRequest;
import br.dev.rodrigopinheiro.estatistica_transacao.adapters.web.mapper.TransacaoWebMapper;
import br.dev.rodrigopinheiro.estatistica_transacao.application.port.in.LimparTransacoesPort;
import br.dev.rodrigopinheiro.estatistica_transacao.application.port.in.RegistrarTransacaoPort;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/transacao")
@Tag(name = "Transações", description = "Operações relacionadas ao registro e gerenciamento de transações financeiras")
public class TransacaoController {

    private final RegistrarTransacaoPort registrarTransacaoPort;
    private final LimparTransacoesPort deletarTransacoesPort;
    private final TransacaoWebMapper transacaoWebMapper;
    
    public TransacaoController(RegistrarTransacaoPort registrarTransacaoPort, LimparTransacoesPort deletarTransacoesPort, TransacaoWebMapper transacaoWebMapper) {
        this.registrarTransacaoPort = registrarTransacaoPort;
        this.deletarTransacoesPort = deletarTransacoesPort;
        this.transacaoWebMapper = transacaoWebMapper;
    }
    

    @Operation(
        summary = "Registra uma nova transação",
        description = "Registra uma transação financeira para cálculo de estatísticas. " +
                     "A transação deve ter valor positivo e data/hora não pode ser futura."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "Transação registrada com sucesso"
        ),
        @ApiResponse(
            responseCode = "422", 
            description = "Dados inválidos (valor negativo, nulo, data futura ou nula)"
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "JSON malformado ou campos obrigatórios ausentes"
        )
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void registrarTransacao(
        @Parameter(description = "Dados da transação a ser registrada", required = true)
        @RequestBody TransacaoRequest request
    ) {
        registrarTransacaoPort.execute(transacaoWebMapper.toDomain(request));
    }

    @Operation(
        summary = "Remove todas as transações",
        description = "Remove todas as transações registradas do sistema. " +
                     "Esta operação é irreversível e afetará o cálculo de estatísticas."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Todas as transações foram removidas com sucesso"
        )
    })
    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void deletarTransacao() {
        deletarTransacoesPort.execute();
    }
}
