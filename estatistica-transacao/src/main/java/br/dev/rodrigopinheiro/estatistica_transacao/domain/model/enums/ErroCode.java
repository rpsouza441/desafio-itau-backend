package br.dev.rodrigopinheiro.estatistica_transacao.domain.model.enums;

public enum ErroCode {
    VALOR_NEGATIVO("VALOR_NEGATIVO", "Valor negativo não é permitido"),
    VALOR_INVALIDO("VALOR_INVALIDO", "Valor não pode ser nulo"),
    DATA_INVALIDA("DATA_INVALIDA", "Data e hora não podem ser nulas"),
    DATA_FUTURA("DATA_FUTURA", "Data e hora não podem ser futuras"),
    JSON_INVALID("JSON_INVALID", "Corpo da requisição inválido"),
    VALIDATION_FAILED("VALIDATION_FAILED", "Validação falhou");

    private final String code;
    private final String message;

    ErroCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String code() {
        return code;
    }

    public String message() {
        return message;
    }
}

