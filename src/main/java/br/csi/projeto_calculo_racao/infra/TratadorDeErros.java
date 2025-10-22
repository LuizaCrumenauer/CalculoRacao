package br.csi.projeto_calculo_racao.infra;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.security.access.AccessDeniedException;

@RestControllerAdvice
public class TratadorDeErros {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<DadosErroValidacao> tratarErro404 (NoSuchElementException ex) {
        var dadosErro = new DadosErroValidacao(null, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(dadosErro);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity tratarErroDadosInvalidos (MethodArgumentNotValidException ex) {
        List<FieldError> erros = ex.getFieldErrors();
        List<DadosErroValidacao> dados = new ArrayList<>();
        for (FieldError fe : erros) {
            dados.add(new DadosErroValidacao(fe.getField(), fe.getDefaultMessage()));
        }
        return ResponseEntity.badRequest().body(dados);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<DadosErroValidacao> tratarErroConflitoDeDados(DataIntegrityViolationException ex) {
        String mensagem = ex.getMessage();
        String campo = "desconhecido";

        if (mensagem.contains("CPF")) {
            campo = "cpf";
        } else if (mensagem.contains("Email")) {
            campo = "email";
        }

        var dadosErro = new DadosErroValidacao(campo, mensagem);

        // Retorna o status 409 Conflict com o objeto de erro no corpo
        return ResponseEntity.status(HttpStatus.CONFLICT).body(dadosErro);
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<DadosErroValidacao> tratarErroFormatoInvalido(HttpMessageNotReadableException ex) {
        String mensagemAmigavel;
        // a causa raiz da exceção com msg especifica
        String causaOriginal = ex.getMostSpecificCause().getMessage();

        if (causaOriginal.contains("LocalDate")) {
            mensagemAmigavel = "Formato de data inválido. O formato esperado é AAAA-MM-DD.";
        } else if (causaOriginal.contains("not one of the values accepted for Enum class")) {
            mensagemAmigavel = "Valor inválido para um campo de seleção (como porte, sexo ou espécie). Verifique os valores permitidos.";
        } else {
            mensagemAmigavel = "A requisição contém um formato de dados inválido.";
        }

        var dadosErro = new DadosErroValidacao(null, mensagemAmigavel);
        return ResponseEntity.badRequest().body(dadosErro);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<DadosErroValidacao> tratarErroGenerico(Exception ex) {
        String mensagem = "Ocorreu um erro inesperado no servidor.";
        ex.printStackTrace();
        var dadosErro = new DadosErroValidacao(null, mensagem);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(dadosErro);
    }

    //tratador para erro de acesso negado
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<DadosErroValidacao> tratarErroAcessoNegado(AccessDeniedException ex) {
        var dadosErro = new DadosErroValidacao(null, "Acesso negado. Você não tem permissão para acessar este recurso.");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(dadosErro);
    }


    private record DadosErroValidacao (String campo, String mensagem) {}
}
