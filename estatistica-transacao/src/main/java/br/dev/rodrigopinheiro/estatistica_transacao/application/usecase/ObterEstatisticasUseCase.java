package br.dev.rodrigopinheiro.estatistica_transacao.application.usecase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.dev.rodrigopinheiro.estatistica_transacao.application.port.in.ObterEstatisticasPort;
import br.dev.rodrigopinheiro.estatistica_transacao.domain.model.Estatistica;
import br.dev.rodrigopinheiro.estatistica_transacao.domain.port.out.TransacaoRepository;
import br.dev.rodrigopinheiro.estatistica_transacao.domain.port.out.EstatisticaRepository;
import br.dev.rodrigopinheiro.estatistica_transacao.domain.service.EstatisticaCalculator;
import br.dev.rodrigopinheiro.estatistica_transacao.infrastructure.config.EstatisticaProperties;
import br.dev.rodrigopinheiro.estatistica_transacao.infrastructure.time.Relogio;

import java.time.Instant;

import org.springframework.stereotype.Service;

@Service
public class ObterEstatisticasUseCase implements ObterEstatisticasPort {
    
    private static final Logger logger = LoggerFactory.getLogger(ObterEstatisticasUseCase.class);
    
    private final TransacaoRepository repository;
    private final Relogio relogio;
    private final EstatisticaProperties estatisticaProperties;
    private final EstatisticaCalculator calculator;

    public ObterEstatisticasUseCase(TransacaoRepository repository, Relogio relogio,
            EstatisticaProperties estatisticaProperties, EstatisticaCalculator calculator) {
        this.repository = repository;
        this.relogio = relogio;
        this.estatisticaProperties = estatisticaProperties;
        this.calculator = calculator;
    }

    @Override
    public Estatistica execute() {
        Instant agora = relogio.agora();
        Instant desde = agora.minusSeconds(estatisticaProperties.getJanelaSegundos());
        
        logger.debug("Iniciando cálculo de estatísticas - Janela: {} segundos, Desde: {}, Até: {}", 
                    estatisticaProperties.getJanelaSegundos(), desde, agora);

        // Strategy Pattern: usa método otimizado se disponível, senão usa abordagem tradicional
        if (repository instanceof EstatisticaRepository estatisticaRepo) {
            logger.debug("Usando estratégia otimizada (EstatisticaRepository) para cálculo");
            // Estratégia otimizada: calcula diretamente dos buckets (O(1))
            Estatistica resultado = estatisticaRepo.calcularEstatisticasSince(desde);
            logger.info("Estatísticas calculadas via estratégia otimizada - Count: {}, Sum: {}, Avg: {}, Min: {}, Max: {}", 
                       resultado.count(), resultado.sum(), resultado.avg(), resultado.min(), resultado.max());
            return resultado;
        } else {
            logger.debug("Usando estratégia tradicional (TransacaoRepository) para cálculo");
            // Estratégia tradicional: busca transações e calcula (O(n))
            Estatistica resultado = calculator.calcular(repository.findSince(desde));
            logger.info("Estatísticas calculadas via estratégia tradicional - Count: {}, Sum: {}, Avg: {}, Min: {}, Max: {}", 
                       resultado.count(), resultado.sum(), resultado.avg(), resultado.min(), resultado.max());
            return resultado;
        }
    }
}
