package br.csi.projeto_calculo_racao.model.tipoRacao;

import br.csi.projeto_calculo_racao.model.pet.Especie;
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
public class TipoRacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tipo;

    @Enumerated(EnumType.STRING)
    private Especie especie;

    private BigDecimal em;

}
