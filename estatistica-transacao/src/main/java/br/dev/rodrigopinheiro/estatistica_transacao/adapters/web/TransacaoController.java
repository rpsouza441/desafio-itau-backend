package br.dev.rodrigopinheiro.estatistica_transacao.adapters.web;


import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import br.dev.rodrigopinheiro.estatistica_transacao.adapters.web.dto.TransacaoRequest;
import br.dev.rodrigopinheiro.estatistica_transacao.adapters.web.mapper.TransacaoWebMapper;
import br.dev.rodrigopinheiro.estatistica_transacao.application.port.in.RegistrarTransacaoPort;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/transacao")
public class TransacaoController {

    private final RegistrarTransacaoPort registrarTransacaoPort;
    private final TransacaoWebMapper transacaoWebMapper;
    
    public TransacaoController(RegistrarTransacaoPort registrarTransacaoPort, TransacaoWebMapper transacaoWebMapper) {
        this.registrarTransacaoPort = registrarTransacaoPort;
        this.transacaoWebMapper = transacaoWebMapper;
    }
    

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void registrarTransacao(@RequestBody @Valid TransacaoRequest request) {
        registrarTransacaoPort.execute(transacaoWebMapper.toDomain(request));
    }
}
