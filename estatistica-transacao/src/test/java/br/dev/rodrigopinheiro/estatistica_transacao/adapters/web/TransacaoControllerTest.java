package br.dev.rodrigopinheiro.estatistica_transacao.adapters.web;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import br.dev.rodrigopinheiro.estatistica_transacao.infrastructure.time.Relogio;
import org.springframework.http.MediaType;

@SpringBootTest
@AutoConfigureMockMvc
public class TransacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private Relogio relogio;

    @BeforeEach
    void setUp() {
        Instant fixed = Instant.parse("2025-10-20T10:15:30Z");
        when(relogio.agora()).thenReturn(fixed);
    }

    @Test
    @DisplayName("Deve Retornar 201 Quando Transacao For Valida")
    void shouldReturn201WhenTransacaoIsValid() throws Exception{
        String jsonString = "{\n" +
            "    \"valor\": 100.00,\n" +
            "    \"dataHora\": \"2025-10-19T10:15:30Z\"\n" +
            "}";

            mockMvc.perform(post("/transacao")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonString))
            .andExpect(status().isCreated())
            .andExpect(content().string(""));
    }

    @Test
    @DisplayName("Deve Retornar 422 Quando Valor For Negativo")
    void shouldReturn422WhenValorIsNegative() throws Exception{
        String jsonString = "{\n" +
            "    \"valor\": -100.00,\n" +
            "    \"dataHora\": \"2025-10-19T10:15:30Z\"\n" +
            "}";

            mockMvc.perform(post("/transacao")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonString))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(content().string(""));
    }

    @Test
    @DisplayName("Deve Retornar 422 Quando DataHora For Futura")
    void shouldReturn422WhenDataHoraIsFuture() throws Exception{
        String jsonString = "{\n" +
            "    \"valor\": 100.00,\n" +
            "    \"dataHora\": \"2025-10-21T10:15:30Z\"\n" +
            "}";

            mockMvc.perform(post("/transacao")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonString))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(content().string(""));
    }

    @Test
    @DisplayName("Deve Retornar 422 Quando DataHora For Nula")
    void shouldReturn422WhenDataHoraIsNull() throws Exception{
        String jsonString = "{\n" +
            "    \"valor\": 100.00,\n" +
            "    \"dataHora\": null\n" +
            "}";

            mockMvc.perform(post("/transacao")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonString))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(content().string(""));
    }

     @Test
    @DisplayName("Deve Retornar 422 Quando Valor For Nulo")
    void shouldReturn422WhenValorIsNull() throws Exception{
        String jsonString = "{\n" +
            "    \"valor\": null,\n" +
            "    \"dataHora\": \"2025-10-19T10:15:30Z\"\n" +
            "}";

            mockMvc.perform(post("/transacao")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonString))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(content().string(""));
    }

    @Test
    @DisplayName("Deve Retornar 400 Quando Json For Invalido")
    void shouldReturn400WhenJsonIsInvalid() throws Exception{
        String jsonString = "{\n" +
            "    \"preco\":abc,\n" +
            "    \"hora\": "+
            "}";

            mockMvc.perform(post("/transacao")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonString))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(""));
    }
    
}
