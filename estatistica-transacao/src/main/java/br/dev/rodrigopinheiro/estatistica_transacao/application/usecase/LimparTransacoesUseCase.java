package br.dev.rodrigopinheiro.estatistica_transacao.application.usecase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.dev.rodrigopinheiro.estatistica_transacao.application.port.in.LimparTransacoesPort;
import br.dev.rodrigopinheiro.estatistica_transacao.domain.port.out.TransacaoRepository;
import org.springframework.stereotype.Service;

@Service
public class LimparTransacoesUseCase implements LimparTransacoesPort {
    
    private static final Logger logger = LoggerFactory.getLogger(LimparTransacoesUseCase.class);
    
    private final TransacaoRepository repository;

    public LimparTransacoesUseCase(TransacaoRepository repository) {
        this.repository = repository;
    }
    
    @Override
    public void execute() {
        logger.info("Iniciando limpeza de todas as transações do repositório");
        
        repository.deleteAll();
        
        logger.info("Limpeza de transações concluída com sucesso");
    }
    
}
