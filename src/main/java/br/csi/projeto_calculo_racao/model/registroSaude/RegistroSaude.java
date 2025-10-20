package br.csi.projeto_calculo_racao.model.registroSaude;

import br.csi.projeto_calculo_racao.model.pet.Pet;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "registro_saude")
@Getter
@Setter
@NoArgsConstructor
public class RegistroSaude {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id")
    @JsonBackReference("pet-saude") // Referência para o "pai" Pet
    private Pet pet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_saude_id")
    private ItemSaude itemSaude;

    @Column(nullable = false)
    private LocalDate data_aplicacao;

    @Column(nullable = true)
    private LocalDate proxima_dose;
}
