package br.dev.rodrigopinheiro.estatistica_transacao.infrastructure.bucket;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BucketEstatistica {

    private long count = 0;
    private BigDecimal sum = BigDecimal.ZERO;
    private BigDecimal min = null;
    private BigDecimal max = null;

    public void addTransacao(BigDecimal valor) {
        count++;
        sum = sum.add(valor);
        if (min == null || valor.compareTo(min) < 0) {
            min = valor;
        }
        if (max == null || valor.compareTo(max) > 0) {
            max = valor;
        }
    }

    public void combinarCom(BucketEstatistica outroBucket) {
        if(outroBucket.count==0) return;

        this.count += outroBucket.count;
        this.sum = this.sum.add(outroBucket.sum);
        if (this.min == null || outroBucket.min.compareTo(this.min) < 0) {
            this.min = outroBucket.min;
        }
        if (this.max == null || outroBucket.max.compareTo(this.max) > 0) {
            this.max = outroBucket.max;
        }
    }

    public Boolean isEmpty() {
        return count == 0;
    }

        // Getters
    public long getCount() { return count; }
    public BigDecimal getSum() { return sum; }
    public BigDecimal getMin() { return count == 0 ? BigDecimal.ZERO : min; }
    public BigDecimal getMax() { return count == 0 ? BigDecimal.ZERO : max; }
    
    public BigDecimal getAvg() {
        if (count == 0) return BigDecimal.ZERO;
        return sum.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
    }
}
