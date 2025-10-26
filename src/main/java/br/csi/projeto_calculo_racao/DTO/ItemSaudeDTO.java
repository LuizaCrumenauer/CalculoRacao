package br.csi.projeto_calculo_racao.DTO;

import br.csi.projeto_calculo_racao.model.registroSaude.TipoItem;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "DTO para criar um novo item de saúde (vacina, remédio, etc.) na lista de itens disponíveis.",
        example = """
                {
                  "nome": "Vacina V10 (Anual)",
                  "tipo": "VACINA"
                }
                """)
public record ItemSaudeDTO(
        @NotBlank(message = "O nome do item é obrigatório")
        String nome,
        @NotNull(message = "O tipo do item é obrigatório")
        TipoItem tipo
) {
}
