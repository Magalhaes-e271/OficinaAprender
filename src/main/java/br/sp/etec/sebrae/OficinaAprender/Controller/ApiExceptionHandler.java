package br.sp.etec.sebrae.OficinaAprender.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);
    private ResponseEntity<?> resposta(int status, String mensagem) {
        return ResponseEntity.status(status).body(Map.of("status", status, "message", mensagem));
    }
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> negocio(ResponseStatusException e) {
        return resposta(e.getStatusCode().value(), e.getReason() == null ? "Não foi possível concluir a operação." : e.getReason());
    }
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<?> tamanho(Exception e) { return resposta(413, "Use arquivos de até 10 MB e até 11 MB no total."); }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> integridade(Exception e) { return resposta(409, "Já existe um registro com estes dados, ou há registros vinculados que impedem a alteração."); }
    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class,
            MissingServletRequestPartException.class, MissingServletRequestParameterException.class})
    public ResponseEntity<?> entrada(Exception e) { return resposta(400, "Os dados enviados estão incompletos ou em formato inválido."); }
    @ExceptionHandler({org.springframework.web.HttpRequestMethodNotSupportedException.class,
            org.springframework.web.HttpMediaTypeNotSupportedException.class,
            org.springframework.web.servlet.resource.NoResourceFoundException.class})
    public ResponseEntity<?> rota(org.springframework.web.ErrorResponse e) {
        return resposta(e.getStatusCode().value(), "Rota, método ou formato de requisição não suportado.");
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> inesperado(Exception e) {
        // Não registrar payloads, credenciais nem parâmetros de conexão.
        log.error("Falha interna ao processar operação: {}", e.getClass().getSimpleName());
        return resposta(500, "Não foi possível concluir a operação. Tente novamente.");
    }
}
