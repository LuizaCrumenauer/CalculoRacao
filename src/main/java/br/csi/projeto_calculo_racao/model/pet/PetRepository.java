package br.csi.projeto_calculo_racao.model.pet;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface PetRepository extends JpaRepository<Pet, Long> {
    Optional<Pet> findByUuid(UUID uuid);
}
