package br.csi.projeto_calculo_racao.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "DTO para registrar uma nova aplicação de item de saúde (vacina, etc.) em um pet.",
        example = """
                {
                  "petUuid": "a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d",
                  "itemSaudeId": 1,
                  "dataAplicacao": "2025-10-26",
                  "proximaDose": "2026-10-26"
                }
                """)
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
