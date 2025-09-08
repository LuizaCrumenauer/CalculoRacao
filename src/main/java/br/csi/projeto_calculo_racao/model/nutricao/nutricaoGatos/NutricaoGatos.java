package br.csi.projeto_calculo_racao.model.nutricao.nutricaoGatos;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "nutricao_gatos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NutricaoGatos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String descricao;
    private String fase_vida;
    private String nivel_atv;
    private BigDecimal coef_min;
    private BigDecimal coef_max;
}
