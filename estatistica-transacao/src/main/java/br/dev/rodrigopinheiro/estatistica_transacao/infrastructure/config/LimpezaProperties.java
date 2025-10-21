package br.dev.rodrigopinheiro.estatistica_transacao.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.limpeza")
public class LimpezaProperties {
    
    private long maxAgeSegundos = 3600;
    private long intervaloMilissegundos = 60000;
    
    public long getMaxAgeSegundos() {
        return maxAgeSegundos;
    }
    
    public void setMaxAgeSegundos(long maxAgeSegundos) {
        this.maxAgeSegundos = maxAgeSegundos;
    }
    
    public long getIntervaloMilissegundos() {
        return intervaloMilissegundos;
    }
    
    public void setIntervaloMilissegundos(long intervaloMilissegundos) {
        this.intervaloMilissegundos = intervaloMilissegundos;
    }
}