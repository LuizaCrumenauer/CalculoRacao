package br.csi.projeto_calculo_racao.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@Schema(description = "DTO com os dados de entrada para realizar um novo cálculo de ração. " +
        "Forneça 'idTipoRacao' OU 'emManual'.",
        example = """
                {
                  "idTipoRacao": 1,
                  "emManual": null,
                  "pesoAtual": 25.5,
                  "nivelAtividade": "Normal"
                }
                """)
public record DadosCalculoDTO(

        @Schema(description = "ID da ração pré-cadastrada (se nulo, 'emManual' deve ser fornecido).", example = "1")
        Long idTipoRacao,

        @Positive(message = "O valor de EM manual deve ser positivo.")
        @Schema(description = "Valor de Energia Metabolizável (EM) manual em kcal/kg (usado se 'idTipoRacao' for nulo).", example = "3850.00")
        BigDecimal emManual,

        @NotNull(message = "O peso atual é obrigatório.")
        @Positive(message = "O peso atual deve ser positivo.")
        @Schema(description = "Peso atual do pet em Kg.", example = "25.5")
        BigDecimal pesoAtual,

        @NotBlank(message = "O nível de atividade é obrigatório.")
        @Schema(description = "Nível de atividade do pet (ex: 'Normal', 'Ativo', 'Leve', 'Castrado').", example = "Normal")
        String nivelAtividade
) {
}
