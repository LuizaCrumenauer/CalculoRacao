package br.csi.projeto_calculo_racao.model.nutricao.nutricaoGatos;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Tabela de referência para coeficientes nutricionais de gatos (NEC), baseada na fase da vida e nível de atividade.")
public class NutricaoGatos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String descricao;
    private String fase_vida;
    private Long idade_meses_min;
    private Long idade_meses_max;
    private String nivel_atv;
    private BigDecimal coef_min;
    private BigDecimal coef_max;
}
