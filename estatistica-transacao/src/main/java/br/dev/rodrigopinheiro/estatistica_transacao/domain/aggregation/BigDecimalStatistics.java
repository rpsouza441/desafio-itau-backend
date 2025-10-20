package br.dev.rodrigopinheiro.estatistica_transacao.domain.aggregation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collector;

public final class BigDecimalStatistics {
    private long count;
    private BigDecimal sum = BigDecimal.ZERO;
    private BigDecimal min = BigDecimal.ZERO;
    private BigDecimal max = BigDecimal.ZERO;
   
    public void accept (BigDecimal value) {
        if (value == null) return;
        if (count == 0) {
            min = value;
            max = value;
        }else{
            if (value.compareTo(min) < 0) min = value;
            if (value.compareTo(max) > 0) max = value;
        }
        sum = sum.add(value);
        count++;
    }

    public void combine (BigDecimalStatistics other) {
        if (other == null || other.count == 0) return;
        if (count == 0) {
            this.count = other.count;
            this.sum = other.sum;
            this.min = other.min;
            this.max = other.max;
            return;
        }
        this.count+=other.count;
        this.sum = this.sum.add(other.sum);
        if (other.min.compareTo(this.min) < 0) this.min = other.min;
        if (other.max.compareTo(this.max) > 0) this.max = other.max;
    }

    public long getCount() {
        return count;
    }

    public BigDecimal getSum() {
        return sum;
    }   

    public BigDecimal getMin() {
        return count==0 ? BigDecimal.ZERO : min;
    }
    public BigDecimal getMax() {
        return count==0 ? BigDecimal.ZERO : max;
    }
    public BigDecimal getAvg(int scale, RoundingMode roundingMode) {
        if (count == 0) return BigDecimal.ZERO.setScale(scale);
        return sum.divide(BigDecimal.valueOf(count), scale, roundingMode);
    }

   public static Collector<BigDecimal, BigDecimalStatistics, BigDecimalStatistics> toCollector() {
        return Collector.of(
            BigDecimalStatistics::new,
            BigDecimalStatistics::accept,
            (left, right) -> { left.combine(right); return left; }
        );
    }

       public static <T> Collector<T, BigDecimalStatistics, BigDecimalStatistics> summarizing(Function<T, BigDecimal> mapper) {
        Objects.requireNonNull(mapper, "mapper");
        return Collector.of(
            BigDecimalStatistics::new,
            (acc, t) -> acc.accept(mapper.apply(t)),
            (left, right) -> { left.combine(right); return left; }
        );
    }

}
