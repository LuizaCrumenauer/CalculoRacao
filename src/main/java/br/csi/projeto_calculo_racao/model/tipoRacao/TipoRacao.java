package br.csi.projeto_calculo_racao.model.tipoRacao;

import br.csi.projeto_calculo_racao.model.pet.Especie;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "tipo_racao")
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Representação de um tipo de ração, contendo sua Energia Metabolizável (EM)")
public class TipoRacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único (numérico) do tipo de ração", example = "1")
    private Long id;

    @Schema(description = "Nome comercial ou descrição da ração", example = "Ração Premium Cães Adultos")
    private String tipo;

    @Schema(description = "Espécie a qual a ração se destina", example = "CACHORRO")
    @Enumerated(EnumType.STRING)
    private Especie especie;

    @Schema(description = "Energia Metabolizável (EM) da ração, em kcal/kg", example = "3850.00")
    private BigDecimal em;

}
