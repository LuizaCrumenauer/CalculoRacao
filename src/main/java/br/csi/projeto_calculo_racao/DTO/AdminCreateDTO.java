package br.csi.projeto_calculo_racao.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AdminCreateDTO(
        @NotBlank @Email String email,
        @NotBlank String senha
) {}
