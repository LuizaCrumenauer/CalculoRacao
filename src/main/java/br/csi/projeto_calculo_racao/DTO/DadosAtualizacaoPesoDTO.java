package br.csi.projeto_calculo_racao.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "DTO para criar ou atualizar um registro de peso com uma data específica (histórico).",
        example = """
                {
                  "peso": 16.2,
                  "data_registro": "2025-10-20"
                }
                """)
public record DadosAtualizacaoPesoDTO(
        @NotNull(message = "O peso é obrigatório.")
        @Positive(message = "O peso deve ser positivo.")
        BigDecimal peso,

        @NotNull(message = "A data de registro é obrigatória.")
        @PastOrPresent(message = "A data de registro não pode ser no futuro.")
        LocalDate data_registro
) {
}
