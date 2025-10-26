package br.csi.projeto_calculo_racao.model.calculo;

import br.csi.projeto_calculo_racao.model.pet.Pet;
import br.csi.projeto_calculo_racao.model.tipoRacao.TipoRacao;
import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Registro de um cálculo de ração realizado") // 2. @Schema da classe
public class Calculo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único (numérico) do cálculo", example = "1")
    private Long id;

    @UuidGenerator
    @Schema(description = "UUID (identificador universal) do cálculo", example = "b1c2d3e4-f5a6-b7c8-d9e0-f1a2b3c4d5e6")
    private UUID uuid;

    @ManyToOne
    @JoinColumn(name = "pet_id")
    @JsonBackReference
    @Schema(description = "Pet ao qual este cálculo pertence")
    private Pet pet;

    @ManyToOne
    @JoinColumn(name = "racao_id")
    @Schema(description = "Ração utilizada neste cálculo")
    private TipoRacao tipoRacao;

    @Schema(description = "Data e hora em que o cálculo foi realizado", example = "2023-10-26T14:30:00")
    private LocalDateTime data_calculo;

    @Schema(description = "Peso do pet no momento do cálculo", example = "15.5")
    private BigDecimal peso_atual;

    @Schema(description = "Idade do pet em anos no momento do cálculo", example = "2")
    private Integer idade;

    @Schema(description = "Idade total do pet em meses no momento do cálculo", example = "24")
    private Long idade_meses_total;

    @Schema(description = "Fase da vida do pet (ex: Adulto, Filhote)", example = "Adulto")
    private String fase_vida;

    @Schema(description = "Nível de atividade do pet (ex: Normal, Ativo)", example = "Normal")
    private String nivel_atv;

    @Schema(description = "Coeficiente MÍNIMO de energia (baseado no nível de atividade/fase da vida)", example = "1.4")
    private BigDecimal coef_min;

    @Schema(description = "Coeficiente MÁXIMO de energia (baseado no nível de atividade/fase da vida)", example = "1.6")
    private BigDecimal coef_max;

    @Schema(description = "Fator de correção aplicado (ex: filhotes = 2.0)", example = "2.0")
    private BigDecimal fator_correcao;

    @Schema(description = "Necessidade Energética de Manutenção (NEM) Média (em kcal/dia)", example = "850.50")
    private BigDecimal nem_media;

    @Schema(description = "Necessidade Energética de Manutenção (NEM) Mínima (em kcal/dia)", example = "800.00")
    private BigDecimal nem_min;

    @Schema(description = "Necessidade Energética de Manutenção (NEM) Máxima (em kcal/dia)", example = "910.75")
    private BigDecimal nem_max;

    @Schema(description = "Energia Metabolizável (EM) da ração selecionada (em kcal/kg)", example = "3750.00")
    private BigDecimal em;

    @Schema(description = "Resultado de um cálculo intermediário (ex: NEM * Fator de Correção)", example = "850.50")
    private BigDecimal resultado;

    @Schema(description = "Quantidade MÍNIMA de ração recomendada (em gramas/dia)", example = "280.50")
    private BigDecimal resultado_min;

    @Schema(description = "Quantidade MÁXIMA de ração recomendada (em gramas/dia)", example = "310.00")
    private BigDecimal resultado_max;
}
