package br.csi.projeto_calculo_racao.model.registroSaude;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemSaudeRepository extends JpaRepository<ItemSaude, Long> {
    // Busca todos os itens onde o tutor_id é NULO (globais)
    // OU onde o tutor_id corresponde ao ID fornecido (do usuário logado).
    @Query("SELECT i FROM ItemSaude i WHERE i.tutor.id = :tutorId OR i.tutor.id IS NULL")
    List<ItemSaude> findGlobalAndTutorItems(Long tutorId);
}
