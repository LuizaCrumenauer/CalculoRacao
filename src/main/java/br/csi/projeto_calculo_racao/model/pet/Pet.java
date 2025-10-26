package br.csi.projeto_calculo_racao.model.pet;

import br.csi.projeto_calculo_racao.model.calculo.Calculo;
import br.csi.projeto_calculo_racao.model.registroPeso.RegistroPeso;
import br.csi.projeto_calculo_racao.model.registroSaude.RegistroSaude;
import br.csi.projeto_calculo_racao.model.tutor.Tutor;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "pet")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Representação de um Pet (animal de estimação)") // 2. @Schema da classe
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único (numérico) do pet", example = "1")
    private Long id;

    @UuidGenerator
    @Schema(description = "UUID (identificador universal) do pet", example = "c1d2e3f4-a5b6-c7d8-e9f0-a1b2c3d4e5f6")
    private UUID uuid;

    @NotBlank
    @Schema(description = "Nome do pet", example = "Rex")
    private String nome;

    @NotNull(message = "A espécie é obrigatória")
    @Enumerated(EnumType.STRING)
    @Schema(description = "Espécie do pet", example = "CACHORRO")
    private Especie especie;

    @Enumerated(EnumType.STRING)
    @Schema(description = "Porte do pet (relevante para cães)", example = "MEDIO")
    private Porte porte;

    @PastOrPresent(message = "A data de nascimento não pode ser uma data futura.")
    @Schema(description = "Data de nascimento do pet", example = "2020-01-15")
    private LocalDate data_nasc;

    @Enumerated(EnumType.STRING)
    @Schema(description = "Sexo do pet", example = "MACHO")
    private Sexo sexo;

    @ManyToOne
    @JoinColumn(name = "tutor_id")
    @JsonBackReference("tutor-pets")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "Tutor ao qual o pet pertence (apenas leitura)")
    private Tutor tutor;

    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @Schema(description = "Lista de cálculos de ração feitos para o pet")
    private List<Calculo> calculos;

    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value="pet-pesos")
    @Schema(description = "Histórico de pesos registrados do pet")
    private List<RegistroPeso> historicoPeso;

    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference("pet-saude") // Referência para os "filhos" (registros de saúde)
    @Schema(description = "Medicamentos/vacinas/etc registrados para o pet")
    private List<RegistroSaude> registrosSaude = new ArrayList<> ();
}
