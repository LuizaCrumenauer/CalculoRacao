package br.csi.projeto_calculo_racao.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record DadosCalculoDTO(

        Long idTipoRacao,

        // Energia Metabolizável manual (opcional)
        @Positive(message = "O valor de EM manual deve ser positivo.")
        BigDecimal emManual,

        // Peso atual do pet (obrigatório)
        @NotNull(message = "O peso atual é obrigatório.")
        @Positive(message = "O peso atual deve ser positivo.")
        BigDecimal pesoAtual,

        // Nível de atividade do pet (obrigatório)
        @NotBlank(message = "O nível de atividade é obrigatório.")
        String nivelAtividade
) {
}
