package br.csi.projeto_calculo_racao.model.registroSaude;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "item_saude")
@Getter
@Setter
@NoArgsConstructor
public class ItemSaude {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do item é obrigatório.")
    @Column(nullable = false, unique = true)
    private String nome;

    @NotNull(message = "O tipo do item é obrigatório.")
    @Enumerated(EnumType.STRING) // Salva o nome do enum ("VACINA") no banco
    @Column(nullable = false)
    private TipoSaude tipo;
}
