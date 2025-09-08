package br.csi.projeto_calculo_racao.model.nutricao.caesAdultos;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "nutricao_caes_adultos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NutricaoCaesAdultos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String descricao;
    private Integer idade_min_anos;
    private Integer idade_max_anos;
    private String nivel_atv;
    private BigDecimal coef_min;
    private BigDecimal coef_max;
}
