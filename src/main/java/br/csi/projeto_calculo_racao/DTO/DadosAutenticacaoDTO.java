package br.csi.projeto_calculo_racao.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Objeto de dados para autenticação (login)")
public record DadosAutenticacaoDTO(

        @Schema(description = "Email de login do usuário", example = "usuario@exemplo.com")
        @NotBlank(message = "O email é obrigatório")
        @Email(message = "Formato de email inválido.")
        String email,

        @Schema(description = "Senha de acesso do usuário", example = "senha123")
        @NotBlank(message = "A senha é obrigatória.")
        String senha) {
}

