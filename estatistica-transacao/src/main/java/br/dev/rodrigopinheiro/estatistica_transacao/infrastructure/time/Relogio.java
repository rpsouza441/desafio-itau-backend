package br.dev.rodrigopinheiro.estatistica_transacao.infrastructure.time;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import org.springframework.stereotype.Component;

@Component
public class Relogio {
  private final Clock clock;

  public Relogio() {
    this.clock = Clock.systemUTC();
  }

  public Relogio(Clock clock) {
    this.clock = clock;
  }

  public Instant agora() {
    return Instant.now(clock);
  }

  public static Relogio fixo(Instant instante) {
    return new Relogio(Clock.fixed(instante, ZoneOffset.UTC));
  }
}