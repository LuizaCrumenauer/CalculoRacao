package br.csi.projeto_calculo_racao.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@Schema(description = "DTO para registrar um novo peso para o pet (a data do registro é definida como a data atual no servidor).",
        example = """
                {
                  "peso": 15.5
                }
                """)
public record DadosRegistroPesoDTO(
        @NotNull(message = "O peso é obrigatório.")
        @Positive(message = "O peso deve ser um valor positivo.")
        BigDecimal peso
) {
}
