package br.dev.rodrigopinheiro.estatistica_transacao.application.exception;

import br.dev.rodrigopinheiro.estatistica_transacao.domain.model.enums.ErroCode;

public class RegraNegocioException extends RuntimeException {
    private final String code;

    public RegraNegocioException(ErroCode errorCode) {
        super(errorCode.message());
        this.code = errorCode.code();
    }

    public String getCode() {
        return code;
    }
}