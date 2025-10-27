package br.dev.rodrigopinheiro.estatistica_transacao.adapters.web.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import br.dev.rodrigopinheiro.estatistica_transacao.application.exception.RegraNegocioException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;



@RestControllerAdvice
public class RestExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(RestExceptionHandler.class);

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<Void> handleRegraNegocioException(RegraNegocioException ex, HttpServletRequest request) {
        logger.warn("Regra de negócio violada - URI: {}, Método: {}, Código: {}, Mensagem: {}, IP: {}", 
                   request.getRequestURI(), 
                   request.getMethod(), 
                   ex.getCode(), 
                   ex.getMessage(),
                   getClientIpAddress(request));
        return ResponseEntity.unprocessableEntity().build();
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Void> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, HttpServletRequest request) {
        logger.warn("JSON inválido recebido - URI: {}, Método: {}, Erro: {}, IP: {}", 
                   request.getRequestURI(), 
                   request.getMethod(), 
                   ex.getMessage(),
                   getClientIpAddress(request));
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        logger.warn("Validação de argumentos falhou - URI: {}, Método: {}, Erros: {}, IP: {}", 
                   request.getRequestURI(), 
                   request.getMethod(), 
                   ex.getBindingResult().getAllErrors(),
                   getClientIpAddress(request));
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Void> handleException(Exception ex, HttpServletRequest request) {
        logger.error("Erro interno do servidor - URI: {}, Método: {}, Erro: {}, IP: {}", 
                    request.getRequestURI(), 
                    request.getMethod(), 
                    ex.getMessage(),
                    getClientIpAddress(request), ex);
        return ResponseEntity.internalServerError().build();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Void> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        logger.warn("Violação de constraint - URI: {}, Método: {}, Violações: {}, IP: {}", 
                   request.getRequestURI(), 
                   request.getMethod(), 
                   ex.getConstraintViolations(),
                   getClientIpAddress(request));
        return ResponseEntity.badRequest().build(); 
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Void> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        logger.warn("Tipo de argumento incompatível - URI: {}, Método: {}, Parâmetro: {}, Valor: {}, Tipo esperado: {}, IP: {}", 
                   request.getRequestURI(), 
                   request.getMethod(), 
                   ex.getName(),
                   ex.getValue(),
                   ex.getRequiredType(),
                   getClientIpAddress(request));
        return ResponseEntity.badRequest().build(); 
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}
