package br.dev.rodrigopinheiro.estatistica_transacao.domain.port.out;

import java.time.Instant;

import br.dev.rodrigopinheiro.estatistica_transacao.domain.model.Estatistica;

/**
 * Interface especializada que estende TransacaoRepository
 * para repositórios que podem calcular estatísticas de forma otimizada.
 * 
 * Segue o Interface Segregation Principle (ISP) do SOLID,
 * permitindo que implementações específicas forneçam métodos otimizados
 * sem afetar a interface base.
 */
public interface EstatisticaRepository extends TransacaoRepository {
    
    /**
     * Calcula estatísticas diretamente dos dados agregados,
     * sem necessidade de reconstruir objetos Transacao.
     * 
     * Este método é uma otimização para repositórios que mantêm
     * dados pré-agregados (como buckets) e podem calcular
     * estatísticas muito mais rapidamente.
     * 
     * @param since momento a partir do qual calcular as estatísticas
     * @return estatísticas calculadas diretamente dos dados agregados
     */
    Estatistica calcularEstatisticasSince(Instant since);
}