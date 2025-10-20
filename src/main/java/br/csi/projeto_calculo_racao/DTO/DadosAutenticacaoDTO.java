package br.csi.projeto_calculo_racao.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record DadosAutenticacaoDTO(

        @NotBlank(message = "O email é obrigatório")
        @Email(message = "Formato de email inválido.")
        String email,

        @NotBlank(message = "A senha é obrigatória.")
        String senha) {
}

