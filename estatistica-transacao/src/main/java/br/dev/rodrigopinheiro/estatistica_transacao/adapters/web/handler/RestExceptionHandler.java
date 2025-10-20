package br.dev.rodrigopinheiro.estatistica_transacao.adapters.web.handler;


import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.dev.rodrigopinheiro.estatistica_transacao.application.exception.RegraNegocioException;
import jakarta.servlet.http.HttpServletRequest;



@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<Void> handleRegraNegocioException(RegraNegocioException ex, HttpServletRequest request) {
        return ResponseEntity.unprocessableEntity().build();
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Void> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, HttpServletRequest request) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Void> handleException(Exception ex, HttpServletRequest request) {  
        return ResponseEntity.internalServerError().build();
    }
}
