package br.dev.rodrigopinheiro.estatistica_transacao.infrastructure.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import br.dev.rodrigopinheiro.estatistica_transacao.domain.port.out.TransacaoRepository;
import br.dev.rodrigopinheiro.estatistica_transacao.infrastructure.repository.BucketTransacaoRepository;
import br.dev.rodrigopinheiro.estatistica_transacao.infrastructure.repository.InMemoryTransacaoRepository;

@Configuration
public class RepositoryConfig {

    @Bean
    @Primary
    @ConditionalOnProperty(name = "app.repository.type", havingValue = "bucket", matchIfMissing = false)
    public TransacaoRepository bucketRepository() {
        return new BucketTransacaoRepository();
    }

    @Bean
    @ConditionalOnProperty(name = "app.repository.type", havingValue = "inmemory", matchIfMissing = true)
    public TransacaoRepository inMemoryRepository() {
        return new InMemoryTransacaoRepository();
    }
}