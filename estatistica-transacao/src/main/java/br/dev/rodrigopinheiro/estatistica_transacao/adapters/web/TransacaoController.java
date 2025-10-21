package br.dev.rodrigopinheiro.estatistica_transacao.adapters.web;


import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import br.dev.rodrigopinheiro.estatistica_transacao.adapters.web.dto.TransacaoRequest;
import br.dev.rodrigopinheiro.estatistica_transacao.adapters.web.mapper.TransacaoWebMapper;
import br.dev.rodrigopinheiro.estatistica_transacao.application.port.in.LimparTransacoesPort;
import br.dev.rodrigopinheiro.estatistica_transacao.application.port.in.RegistrarTransacaoPort;


@RestController
@RequestMapping("/transacao")
public class TransacaoController {

    private final RegistrarTransacaoPort registrarTransacaoPort;
    private final LimparTransacoesPort deletarTransacoesPort;
    private final TransacaoWebMapper transacaoWebMapper;
    
    public TransacaoController(RegistrarTransacaoPort registrarTransacaoPort, LimparTransacoesPort deletarTransacoesPort, TransacaoWebMapper transacaoWebMapper) {
        this.registrarTransacaoPort = registrarTransacaoPort;
        this.deletarTransacoesPort = deletarTransacoesPort;
        this.transacaoWebMapper = transacaoWebMapper;
    }
    

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void registrarTransacao(@RequestBody TransacaoRequest request) {
        registrarTransacaoPort.execute(transacaoWebMapper.toDomain(request));
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void deletarTransacao() {
        deletarTransacoesPort.execute();
    }
}
