package br.csi.projeto_calculo_racao.model.registroSaude;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistroSaudeRepository extends JpaRepository<RegistroSaude, Long> {
}
