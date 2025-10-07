package br.csi.projeto_calculo_racao.model.calculo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CalculoRepository extends JpaRepository<Calculo, Long> {
}
