package br.dev.rodrigopinheiro.estatistica_transacao.application.usecase;

import br.dev.rodrigopinheiro.estatistica_transacao.application.port.in.LimparTransacoesPort;
import br.dev.rodrigopinheiro.estatistica_transacao.domain.port.TransacaoRepository;
import org.springframework.stereotype.Service;

@Service
public class LimparTransacoesUseCase implements LimparTransacoesPort {
    private final TransacaoRepository repository;

    public LimparTransacoesUseCase(TransacaoRepository repository) {
        this.repository = repository;
    }
    @Override
    public void execute() {
        repository.deleteAll();
    }
    
}
