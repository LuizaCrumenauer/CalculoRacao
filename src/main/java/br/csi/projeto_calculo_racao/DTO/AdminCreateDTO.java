package br.csi.projeto_calculo_racao.DTO;

import io.swagger.v3.oas.annotations.media.Schema; // 1. Importar
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Dados necessários para criar um novo usuário Administrador",
        example = "{\"email\": \"novo_admin@email.com\", \"senha\": \"senhaAdmin123\"}") // 2. Exemplo
public record AdminCreateDTO(
        @NotBlank @Email
        @Schema(description = "Email de login para o novo admin", example = "novo_admin@email.com") // 3. Campos
        String email,

        @NotBlank
        @Schema(description = "Senha de acesso para o novo admin", example = "senhaAdmin123")
        String senha
) {}
