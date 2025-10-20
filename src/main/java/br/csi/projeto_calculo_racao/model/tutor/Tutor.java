package br.csi.projeto_calculo_racao.model.tutor;

import br.csi.projeto_calculo_racao.model.pet.Pet;
import br.csi.projeto_calculo_racao.model.usuario.Usuario;
import br.csi.projeto_calculo_racao.util.CpfUtils;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;


import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tutor")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Tutor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @UuidGenerator
    private UUID uuid;

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotBlank(message = "CPF é obrigatório")
    @Pattern(
            regexp = "\\d{11}|\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}",
            message = "CPF deve estar no formato 00000000000 ou 000.000.000-00"
    )
    private String cpf;


    @Pattern(
            regexp = "\\(?\\d{2}\\)?\\s?\\d{4,5}-\\d{4}",
            message = "Telefone inválido"
    )
    private String telefone;

    @Embedded
    private Endereco endereco;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("tutor-pets")
    private Usuario usuario;

    @OneToMany(mappedBy = "tutor", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Pet> pets;

//remove pontos e traços
    @PrePersist
    @PreUpdate
    private void normalizeCpf() {
        this.cpf = CpfUtils.limpar(this.cpf);
    }
}
