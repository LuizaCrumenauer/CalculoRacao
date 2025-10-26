package br.csi.projeto_calculo_racao.model.tutor;

import br.csi.projeto_calculo_racao.model.pet.Pet;
import br.csi.projeto_calculo_racao.model.usuario.Usuario;
import br.csi.projeto_calculo_racao.util.CpfUtils;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Representação do Tutor (dono do pet)")
public class Tutor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único (numérico) do tutor", example = "1")
    private Long id;

    @UuidGenerator
    @Schema(description = "UUID (identificador universal) do tutor", example = "f4e5f6a7-b8c9-d0e1-f2a3-b4c5d6e7f8a9")
    private UUID uuid;

    @NotBlank(message = "Nome é obrigatório")
    @Schema(description = "Nome completo do tutor", example = "João da Silva")
    private String nome;

    @NotBlank(message = "CPF é obrigatório")
    @Pattern(
            regexp = "\\d{11}|\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}",
            message = "CPF deve estar no formato 00000000000 ou 000.000.000-00"
    )
    @Schema(description = "CPF do tutor (pode ou não ter formatação)", example = "123.456.789-00 ou 12345678900")
    private String cpf;


    @Pattern(
            regexp = "\\(?\\d{2}\\)?\\s?\\d{4,5}-\\d{4}",
            message = "Telefone inválido"
    )
    @Schema(description = "Telefone de contato do tutor (opcional)", example = "(55) 99988-7766")
    private String telefone;

    @Embedded
    @Schema(description = "Endereço completo do tutor")
    private Endereco endereco;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("tutor-pets")
    @Schema(description = "Conta de usuário associada a este perfil de tutor (apenas leitura)")
    private Usuario usuario;

    @OneToMany(mappedBy = "tutor", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @Schema(description = "Lista de pets cadastrados para este tutor")
    private List<Pet> pets;

//remove pontos e traços
    @PrePersist
    @PreUpdate
    private void normalizeCpf() {
        this.cpf = CpfUtils.limpar(this.cpf);
    }
}
