package br.csi.projeto_calculo_racao.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DadosRegistroPesoDTO(
        @NotNull(message = "O peso é obrigatório.")
        @Positive(message = "O peso deve ser um valor positivo.")
        BigDecimal peso
) {
}
