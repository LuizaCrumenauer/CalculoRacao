package br.csi.projeto_calculo_racao.DTO;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record RegistroSaudeDTO(
        @NotNull(message = "O ID do pet é obrigatório")
        UUID petUuid,

        @NotNull(message = "O ID do item de saúde é obrigatório")
        Long itemSaudeId,

        @NotNull(message = "A data de aplicação é obrigatória")
        LocalDate dataAplicacao,

        LocalDate proximaDose // Opcional
) {
}
