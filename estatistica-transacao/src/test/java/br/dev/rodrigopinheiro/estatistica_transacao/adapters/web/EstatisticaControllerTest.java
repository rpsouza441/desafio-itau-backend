package br.dev.rodrigopinheiro.estatistica_transacao.adapters.web;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import java.math.BigDecimal;
import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import br.dev.rodrigopinheiro.estatistica_transacao.domain.model.Transacao;
import br.dev.rodrigopinheiro.estatistica_transacao.domain.port.out.TransacaoRepository;
import br.dev.rodrigopinheiro.estatistica_transacao.infrastructure.time.Relogio;

@SpringBootTest
@AutoConfigureMockMvc
public class EstatisticaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TransacaoRepository transacaoRepository;

    @MockitoBean
    private Relogio relogio;

    private Instant instanteFixo;

    @BeforeEach
    void setUp() {
        instanteFixo = Instant.parse("2025-10-20T12:15:30Z");
        when(relogio.agora()).thenReturn(instanteFixo);
        transacaoRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve Retornar Estatistica Zeradas Quando Nao Houver Transacoes")
    void shouldReturnZeroStatisticsWhenNoTransactions() throws Exception {
        mockMvc.perform(get("/estatistica"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                            "count": 0,
                            "sum": 0.0,
                            "avg": 0.0,
                            "min": 0.0,
                            "max": 0.0
                        }
                        """));
    }

    @Test
    @DisplayName("Deve calcular estatísticas corretamente com transações válidas")
    void shouldReturnCorrectStatisticsWithValidTransactions() throws Exception {
        // Transações dentro da janela de 60s
        Instant dentro60s1 = instanteFixo.minusSeconds(30); // 30s atrás
        Instant dentro60s2 = instanteFixo.minusSeconds(45); // 45s atrás
        Instant dentro60s3 = instanteFixo.minusSeconds(10); // 10s atrás

        transacaoRepository.save(new Transacao(new BigDecimal("100.00"), dentro60s1));
        transacaoRepository.save(new Transacao(new BigDecimal("200.00"), dentro60s2));
        transacaoRepository.save(new Transacao(new BigDecimal("50.00"), dentro60s3));

        // Transação fora da janela (não deve ser incluída)
        Instant fora60s = instanteFixo.minusSeconds(70);
        transacaoRepository.save(new Transacao(new BigDecimal("999.00"), fora60s));

        // Act & Assert
        mockMvc.perform(get("/estatistica"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                            "count": 3,
                            "sum": 350.00,
                            "avg": 116.67,
                            "min": 50.00,
                            "max": 200.00
                        }
                        """)); // 350/3 = 116.67
    }

    @Test
    @DisplayName("Deve Usar Janela Customizada quando fornecida")
    void shouldReturnCorrectStatisticsWithCustomWindow() throws Exception {
        // Transações dentro da janela de 60s
        Instant dentro120s = instanteFixo.minusSeconds(90); // 30s atrás

        transacaoRepository.save(new Transacao(new BigDecimal("100.00"), dentro120s));

        // Transação fora da janela (não deve ser incluída)
        Instant fora120s = instanteFixo.minusSeconds(150);
        transacaoRepository.save(new Transacao(new BigDecimal("999.00"), fora120s));

        // Act & Assert
        mockMvc.perform(get("/estatistica?janela=120"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                            "count": 1,
                            "sum": 100.00,
                            "avg": 100.00,
                            "min": 100.00,
                            "max": 100.00
                        }
                        """));
    }

    @Test
    @DisplayName("Inclui transação exatamente na borda da janela de 60s (filtro inclusivo)")
    void shouldIncludeTransactionExactlyAtWindowBoundaryWhenInclusive() throws Exception {
        // Arrange: transação exatamente 60s atrás
        Instant naBorda = instanteFixo.minusSeconds(60);
        transacaoRepository.save(new Transacao(new BigDecimal("100.00"), naBorda));

        // Act & Assert
        mockMvc.perform(get("/estatistica"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                            {
                                "count": 1,
                                "sum": 100.00,
                                "avg": 100.00,
                                "min": 100.00,
                                "max": 100.00
                            }
                        """));
    }

    @Test
    @DisplayName("Deve retornar 400 para janela com valor zero")
    void shouldReturn400ForZeroWindow() throws Exception {
        mockMvc.perform(get("/estatistica?janela=0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar 400 para janela com valor negativo")
    void shouldReturn400ForNegativeWindow() throws Exception {
        mockMvc.perform(get("/estatistica?janela=-30"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar 400 para janela maior que 3600 segundos")
    void shouldReturn400ForWindowGreaterThan3600() throws Exception {
        mockMvc.perform(get("/estatistica?janela=4000"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar 400 para janela com valor não numérico")
    void shouldReturn400ForNonNumericWindow() throws Exception {
        mockMvc.perform(get("/estatistica?janela=abc"))
                .andExpect(status().isBadRequest());
    }

}
