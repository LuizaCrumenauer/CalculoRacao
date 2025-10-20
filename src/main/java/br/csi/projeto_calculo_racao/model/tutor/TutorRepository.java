package br.csi.projeto_calculo_racao.model.tutor;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TutorRepository extends JpaRepository<Tutor, Long> {
    Optional<Tutor> findByUuid( UUID uuid);
    Optional<Tutor> findByCpf(String cpf);
    Optional<Tutor> findByUsuario_Email ( String email);
}

