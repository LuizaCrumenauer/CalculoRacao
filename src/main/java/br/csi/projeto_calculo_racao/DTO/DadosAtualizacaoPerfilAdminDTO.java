package br.csi.projeto_calculo_racao.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Dados para atualização do perfil do Admin (email ou senha). 'senhaAtual' é sempre obrigatória.",
        example = "{\"email\": \"admin_atual@email.com\", \"novoEmail\": \"admin_novo@email.com\", \"novaSenha\": null, \"senhaAtual\": \"senhaAntiga123\"}")
public record DadosAtualizacaoPerfilAdminDTO(
        @Email @NotBlank
        @Schema(description = "Email atual do administrador logado (usado para identificação)",
                example = "admin_atual@email.com") // 3. Campos
        String email,

        @Email(message = "Formato de email inválido")
        @Schema(description = "Novo email desejado (opcional, deixe nulo se não quiser mudar)",
                example = "admin_novo@email.com")
        String novoEmail,

        @Schema(description = "Nova senha desejada (opcional, deixe nulo se não quiser mudar)",
                example = "novaSenhaSuperForte456")
        String novaSenha,

        @NotBlank(message = "A senha atual é obrigatória para confirmação")
        @Schema(description = "Senha atual do administrador (obrigatória para confirmar qualquer alteração)",
                example = "senhaAntiga123")
        String senhaAtual
) {}
