package br.csi.projeto_calculo_racao.model.registroPeso;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistroPesoRepository extends JpaRepository<RegistroPeso, Long> {
}
