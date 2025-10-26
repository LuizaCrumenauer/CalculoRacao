package br.csi.projeto_calculo_racao.DTO;

import br.csi.projeto_calculo_racao.model.tutor.Endereco;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "DTO para cadastro completo de um novo Tutor",
        example = """
                {
                  "nome": "João da Silva",
                  "cpf": "12345678901",
                  "telefone": "55999887766",
                  "endereco": {
                    "logradouro": "Rua das Flores",
                    "numero": "123",
                    "complemento": "Apto 405",
                    "bairro": "Centro",
                    "cidade": "Santa Maria",
                    "uf": "RS",
                    "cep": "97010-000"
                  },
                  "email": "joao.silva@exemplo.com",
                  "senha": "senhaForte123"
                }
                """)
public record DadosCadastroTutorDTO(
        @NotBlank(message = "Nome é obrigatório")
        @Schema(description = "Nome completo do tutor", example = "João da Silva")
        String nome,

        @NotBlank(message = "CPF é obrigatório")
        @Schema(description = "CPF do tutor (somente números)", example = "12345678901")
        String cpf,

        @Schema(description = "Telefone de contato (opcional)", example = "55999887766")
        String telefone,

        @NotNull(message = "O endereço é obrigatório")
        @Valid // Valida os campos dentro do objeto Endereco
        @Schema(description = "Endereço completo do tutor") // 4. Campo complexo (objeto)
        Endereco endereco,

        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Formato de email inválido")
        @Schema(description = "Email que será usado para o login", example = "joao.silva@exemplo.com")
        String email,

        @NotBlank(message = "Senha é obrigatória")
        @Schema(description = "Senha de acesso", example = "senhaForte123")
        String senha
) {
}
