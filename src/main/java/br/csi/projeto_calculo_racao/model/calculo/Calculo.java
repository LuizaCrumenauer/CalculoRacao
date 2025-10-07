package br.csi.projeto_calculo_racao.model.calculo;

import br.csi.projeto_calculo_racao.model.pet.Pet;
import br.csi.projeto_calculo_racao.model.tipoRacao.TipoRacao;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "calculo")
@Getter
@Setter
@NoArgsConstructor
public class Calculo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @UuidGenerator
    private UUID uuid;

    @ManyToOne
    @JoinColumn(name = "pet_id")
    @JsonBackReference
    private Pet pet;

    @ManyToOne
    @JoinColumn(name = "racao_id")
    private TipoRacao tipoRacao;

    private LocalDateTime data_calculo;
    private BigDecimal peso_atual;
    private Integer idade;
    private String fase_vida;
    private String nivel_atv;
    private BigDecimal coef_min;
    private BigDecimal coef_max;
    private BigDecimal nem_media;
    private BigDecimal nem_min;
    private BigDecimal nem_max;
    private BigDecimal em;
    private BigDecimal resultado;
    private BigDecimal resultado_min;
    private BigDecimal resultado_max;
}
