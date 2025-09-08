package br.csi.projeto_calculo_racao.model.nutricao.caesFilhotes;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "nutricao_caes_filhotes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NutricaoCaesFilhotes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String porte_adulto;
    private Integer idade_meses_min;
    private Integer idade_meses_max;
    private BigDecimal fator_correcao;
}
