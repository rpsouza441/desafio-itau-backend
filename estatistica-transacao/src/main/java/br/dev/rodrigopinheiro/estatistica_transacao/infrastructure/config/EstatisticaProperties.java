package br.dev.rodrigopinheiro.estatistica_transacao.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.estatistica")
public class EstatisticaProperties {
    
    private int janelaSegundos = 60;
    private int maxJanelaSegundos = 3600;
    
    public int getJanelaSegundos() {
        return janelaSegundos;
    }
    
    public void setJanelaSegundos(int janelaSegundos) {
        this.janelaSegundos = janelaSegundos;
    }
    
    public int getMaxJanelaSegundos() {
        return maxJanelaSegundos;
    }
    
    public void setMaxJanelaSegundos(int maxJanelaSegundos) {
        this.maxJanelaSegundos = maxJanelaSegundos;
    }
}