package br.csi.projeto_calculo_racao.model.tutor;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Objeto embutível representando o endereço do tutor")
public class Endereco {
    @NotBlank
    @Schema(description = "Nome da rua, avenida, etc.", example = "Rua das Flores")
    private String logradouro;
    @NotBlank
    @Schema(description = "Número da residência", example = "123")
    private String numero;

    @Schema(description = "Complemento do endereço (apartamento, bloco, etc.)", example = "Apto 405")
    private String complemento;
    @NotBlank
    @Schema(description = "Bairro da residência", example = "Centro")
    private String bairro;
    @NotBlank
    @Schema(description = "Cidade da residência", example = "Santa Maria")
    private String cidade;
    @NotBlank
    @Schema(description = "Sigla da Unidade Federativa (Estado)", example = "RS")
    private String uf;
    @NotBlank
    @Size(min = 8, max = 9, message = "CEP Inválido")
    @Schema(description = "Código de Endereçamento Postal (CEP)", example = "97010-000")
    private String cep;
}
