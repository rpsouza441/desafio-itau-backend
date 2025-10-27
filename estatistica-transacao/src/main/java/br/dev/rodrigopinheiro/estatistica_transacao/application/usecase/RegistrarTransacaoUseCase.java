package br.dev.rodrigopinheiro.estatistica_transacao.application.usecase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.dev.rodrigopinheiro.estatistica_transacao.domain.model.Transacao;
import br.dev.rodrigopinheiro.estatistica_transacao.domain.model.enums.ErroCode;
import br.dev.rodrigopinheiro.estatistica_transacao.domain.port.out.TransacaoRepository;
import br.dev.rodrigopinheiro.estatistica_transacao.infrastructure.time.Relogio;

import org.springframework.stereotype.Service;

import br.dev.rodrigopinheiro.estatistica_transacao.application.exception.RegraNegocioException;
import br.dev.rodrigopinheiro.estatistica_transacao.application.port.in.RegistrarTransacaoPort;

@Service
public class RegistrarTransacaoUseCase implements RegistrarTransacaoPort{
    
    private static final Logger logger = LoggerFactory.getLogger(RegistrarTransacaoUseCase.class);
    
    private final TransacaoRepository repo;
    private final Relogio relogio;
    
    public RegistrarTransacaoUseCase(TransacaoRepository repo, Relogio relogio) {
        this.repo = repo;
        this.relogio = relogio;
    }

    public void execute(Transacao transacao) {
        logger.debug("Iniciando validação de transação - Valor: {}, DataHora: {}", 
                    transacao.valor(), transacao.dataHora());
        
        if(transacao.valor()==null) {
            logger.warn("Tentativa de registro com valor nulo");
            throw new RegraNegocioException(ErroCode.VALOR_INVALIDO);
        }
        if(transacao.dataHora()==null) {
            logger.warn("Tentativa de registro com data nula");
            throw new RegraNegocioException(ErroCode.DATA_INVALIDA);
        }
        if (transacao.valor().signum() < 0) {
            logger.warn("Tentativa de registro com valor negativo: {}", transacao.valor());
            throw new RegraNegocioException(ErroCode.VALOR_NEGATIVO);
        }
        if (transacao.dataHora().isAfter(relogio.agora())) {
            logger.warn("Tentativa de registro com data futura: {}", transacao.dataHora());
            throw new RegraNegocioException(ErroCode.DATA_FUTURA);
        }
    
        logger.debug("Validação concluída, salvando transação no repositório");
        repo.save(transacao);
        logger.info("Transação salva com sucesso no repositório - Valor: {}, DataHora: {}", 
                   transacao.valor(), transacao.dataHora());
    }
}
