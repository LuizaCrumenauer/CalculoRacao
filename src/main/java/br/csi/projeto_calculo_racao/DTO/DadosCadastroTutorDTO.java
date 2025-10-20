package br.csi.projeto_calculo_racao.DTO;

import br.csi.projeto_calculo_racao.model.tutor.Endereco;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// DTO para receber os dados de cadastro do Tutor e do Usuário associado.
public record DadosCadastroTutorDTO(
        @NotBlank(message = "Nome é obrigatório")
        String nome,

        @NotBlank(message = "CPF é obrigatório")
        String cpf,

        String telefone,

        @NotNull(message = "O endereço é obrigatório")
        @Valid // Valida os campos dentro do objeto Endereco
        Endereco endereco,

        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Formato de email inválido")
        String email,

        @NotBlank(message = "Senha é obrigatória")
        String senha
) {
}
