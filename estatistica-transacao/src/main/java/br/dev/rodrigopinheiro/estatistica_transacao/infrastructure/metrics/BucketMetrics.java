package br.dev.rodrigopinheiro.estatistica_transacao.infrastructure.metrics;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import br.dev.rodrigopinheiro.estatistica_transacao.infrastructure.repository.BucketTransacaoRepository;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Component
@Endpoint(id = "buckets")
@ConditionalOnProperty(name = "app.repository.type", havingValue = "bucket", matchIfMissing = false)
public class BucketMetrics {

    private final BucketTransacaoRepository repository;

    public BucketMetrics(BucketTransacaoRepository repository) {
        this.repository = repository;
    }

    @ReadOperation
    public BucketInfo getBucketInfo() {
        return new BucketInfo(
            repository.getBucketCount(),
            Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
        );
    }

    public record BucketInfo(int bucketCount, long memoryUsed) {}
}