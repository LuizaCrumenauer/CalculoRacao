package br.csi.projeto_calculo_racao.model.registroPeso;

import br.csi.projeto_calculo_racao.model.pet.Pet;
import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
@Schema(description = "Registro pontual do peso de um pet") // 2. @Schema da classe
public class RegistroPeso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único (numérico) do registro", example = "1")
    private Long id;

    @NotNull(message = "O peso é obrigatório")
    @Schema(description = "Peso registrado (em Kg)", example = "15.7")
    private BigDecimal peso;

    @NotNull(message = "A data é obrigatória")
    @Schema(description = "Data em que o peso foi registrado", example = "2023-10-20")
    private LocalDate data_registro;

    @ManyToOne
    @JoinColumn(name = "pet_id", nullable = false)
    @JsonBackReference(value="pet-pesos")
    @Schema(description = "Pet ao qual este registro pertence")
    private Pet pet;
}
