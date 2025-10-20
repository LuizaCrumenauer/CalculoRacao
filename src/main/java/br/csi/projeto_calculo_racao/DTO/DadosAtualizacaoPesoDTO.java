package br.csi.projeto_calculo_racao.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DadosAtualizacaoPesoDTO(
        @NotNull(message = "O peso é obrigatório.")
        @Positive(message = "O peso deve ser positivo.")
        BigDecimal peso,

        @NotNull(message = "A data de registro é obrigatória.")
        @PastOrPresent(message = "A data de registro não pode ser no futuro.")
        LocalDate data_registro
) {
}
