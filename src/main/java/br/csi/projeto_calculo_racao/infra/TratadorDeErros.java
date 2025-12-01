package br.csi.projeto_calculo_racao.infra;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

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
        String mensagem = ex.getMostSpecificCause().getMessage();
        String campo = "desconhecido";
        String mensagemAmigavel = "Conflito de dados.";

        if (mensagem.contains("CPF")) {
            campo = "cpf";
            mensagemAmigavel = "Este CPF já está cadastrado.";
        } else if (mensagem.contains("Email")) {
            campo = "email";
            mensagemAmigavel = "Este email já está em uso.";
        }
        else if (mensagem.contains("atualização ou exclusão em tabela \"item_saude\" viola restrição de chave estrangeira \"registro_saude_item_saude_id_fkey\" em \"registro_saude\"")) {
            mensagemAmigavel = "Não é possível excluir este item pois ele já está vinculado a um ou mais registros de saúde.";
        }

        var dadosErro = new DadosErroValidacao(campo, mensagemAmigavel);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(dadosErro);
    }

    @ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> tratarConstraintViolation(jakarta.validation.ConstraintViolationException ex) {
        // Itera sobre as violações e cria uma lista de erros
        var erros = ex.getConstraintViolations().stream()
                .map(violacao -> new DadosErroValidacao(violacao.getPropertyPath().toString(), violacao.getMessage()))
                .toList();

        return ResponseEntity.badRequest().body(erros);
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<DadosErroValidacao> tratarErroFormatoInvalido(HttpMessageNotReadableException ex) {
        String mensagemAmigavel;
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

    //erro de login
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<DadosErroValidacao> tratarErroCredenciaisInvalidas( BadCredentialsException ex) {
        // Retorna 401 Unauthorized com a mensagem personalizada
        var dadosErro = new DadosErroValidacao(null, "Usuário ou senha inválido.");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(dadosErro);
    }

//    //tratador para erro de acesso negado
//    @ExceptionHandler(AccessDeniedException.class)
//    public ResponseEntity<DadosErroValidacao> tratarErroAcessoNegado(AccessDeniedException ex) {
//        var dadosErro = new DadosErroValidacao(null, "Acesso negado. Você não tem permissão para acessar este recurso.");
//        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(dadosErro);
//    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<DadosErroValidacao> tratarErroGenerico(Exception ex) {
        String mensagem = "Ocorreu um erro inesperado no servidor.";
        ex.printStackTrace();
        var dadosErro = new DadosErroValidacao(null, mensagem);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(dadosErro);
    }


    private record DadosErroValidacao (String campo, String mensagem) {}
}
