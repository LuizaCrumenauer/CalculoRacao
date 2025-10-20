package br.csi.projeto_calculo_racao.DTO;

import br.csi.projeto_calculo_racao.model.registroSaude.TipoItem;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ItemSaudeDTO(
        @NotBlank(message = "O nome do item é obrigatório")
        String nome,
        @NotNull(message = "O tipo do item é obrigatório")
        TipoItem tipo
) {
}
