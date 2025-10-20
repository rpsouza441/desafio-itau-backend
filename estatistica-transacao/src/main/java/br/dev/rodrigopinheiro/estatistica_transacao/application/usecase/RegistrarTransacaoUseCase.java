package br.dev.rodrigopinheiro.estatistica_transacao.application.usecase;


import br.dev.rodrigopinheiro.estatistica_transacao.domain.model.Transacao;
import br.dev.rodrigopinheiro.estatistica_transacao.domain.model.enums.ErroCode;
import br.dev.rodrigopinheiro.estatistica_transacao.domain.port.out.TransacaoRepository;
import br.dev.rodrigopinheiro.estatistica_transacao.infrastructure.time.Relogio;

import org.springframework.stereotype.Service;

import br.dev.rodrigopinheiro.estatistica_transacao.application.exception.RegraNegocioException;
import br.dev.rodrigopinheiro.estatistica_transacao.application.port.in.RegistrarTransacaoPort;

@Service
public class RegistrarTransacaoUseCase implements RegistrarTransacaoPort{
    private final TransacaoRepository repo;
    private final Relogio relogio;
    public RegistrarTransacaoUseCase(TransacaoRepository repo, Relogio relogio) {
        this.repo = repo;
        this.relogio = relogio;
    }

    public void execute(Transacao transacao) {
        if(transacao.valor()==null) throw new RegraNegocioException(ErroCode.VALOR_INVALIDO);
        if(transacao.dataHora()==null) throw new RegraNegocioException(ErroCode.DATA_INVALIDA);
        if (transacao.valor().signum() < 0) throw new RegraNegocioException(ErroCode.VALOR_NEGATIVO);
        if (transacao.dataHora().isAfter(relogio.agora())) throw new RegraNegocioException(ErroCode.DATA_FUTURA);
    
        repo.save(transacao);
    }
}
