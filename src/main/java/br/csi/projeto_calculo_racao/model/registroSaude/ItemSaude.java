package br.csi.projeto_calculo_racao.model.registroSaude;

import br.csi.projeto_calculo_racao.model.tutor.Tutor;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private TipoItem tipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutor_id")
    @JsonIgnore
    private Tutor tutor; //se nulo item do admin
}
