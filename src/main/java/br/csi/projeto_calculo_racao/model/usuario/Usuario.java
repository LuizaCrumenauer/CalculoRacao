package br.csi.projeto_calculo_racao.model.usuario;

// 1. Importar as anotações necessárias
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Representação de um Usuário do sistema (conta de acesso)")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único (numérico) do usuário no banco", example = "1")
    private Long id;

    @UuidGenerator
    @Schema(description = "UUID (identificador universal) do usuário, usado para APIs", example = "a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d")
    private UUID uuid;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    @Schema(description = "Email de login do usuário (deve ser único)", example = "usuario@exemplo.com")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Schema(description = "Senha de acesso (apenas para escrita, nunca retornada)", example = "senha123")
    private String senha;

    @NotNull(message = "O papel do usuário é obrigatório")
    @Enumerated(EnumType.STRING)
    @Schema(description = "Nível de permissão do usuário", example = "USER")
    private Role role;

}
