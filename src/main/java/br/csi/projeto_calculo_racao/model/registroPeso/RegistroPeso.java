package br.csi.projeto_calculo_racao.model.registroPeso;

import br.csi.projeto_calculo_racao.model.pet.Pet;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "registro_peso")
@Getter
@Setter
@NoArgsConstructor
public class RegistroPeso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O peso é obrigatório")
    private BigDecimal peso;

    @NotBlank(message = "A data é obrigatória")
    private LocalDate data_registro;

    @ManyToOne
    @JoinColumn(name = "pet_id", nullable = false)
    @JsonBackReference(value="pet-pesos")
    private Pet pet;
}
