package br.dev.rodrigopinheiro.estatistica_transacao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class EstatisticaTransacaoApplication {

	public static void main(String[] args) {
		SpringApplication.run(EstatisticaTransacaoApplication.class, args);
	}

}
