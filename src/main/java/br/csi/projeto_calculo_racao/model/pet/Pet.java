package br.csi.projeto_calculo_racao.model.pet;

import br.csi.projeto_calculo_racao.model.calculo.Calculo;
import br.csi.projeto_calculo_racao.model.registroPeso.RegistroPeso;
import br.csi.projeto_calculo_racao.model.registroSaude.RegistroSaude;
import br.csi.projeto_calculo_racao.model.tutor.Tutor;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @UuidGenerator
    private UUID uuid;

    @NotBlank
    private String nome;

    @NotNull(message = "A espécie é obrigatória")
    @Enumerated(EnumType.STRING)
    private Especie especie;

    @Enumerated(EnumType.STRING)
    private Porte porte;

    @PastOrPresent(message = "A data de nascimento não pode ser uma data futura.")
    private LocalDate data_nasc;

    @Enumerated(EnumType.STRING)
    private Sexo sexo;

    @ManyToOne
    @JoinColumn(name = "tutor_id")
    @JsonBackReference("tutor-pets")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Tutor tutor;

    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Calculo> calculos;

    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value="pet-pesos")
    private List<RegistroPeso> historicoPeso;

    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference("pet-saude") // Referência para os "filhos" (registros de saúde)
    private List<RegistroSaude> registrosSaude = new ArrayList<> ();
}
