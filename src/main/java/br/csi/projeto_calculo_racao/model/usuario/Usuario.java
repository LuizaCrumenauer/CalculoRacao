package br.csi.projeto_calculo_racao.model.usuario;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

//    id SERIAL PRIMARY KEY,
//    uuid UUID DEFAULT gen_random_uuid(),
//    email VARCHAR (100) NOT NULL UNIQUE,
//    senha VARCHAR(255) NOT NULL,
//    role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN', 'USER'))

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @UuidGenerator
    private UUID uuid;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    private String senha;

    @NotNull(message = "O papel do usuário é obrigatório")
    @Enumerated(EnumType.STRING)
    private Role role;

}
