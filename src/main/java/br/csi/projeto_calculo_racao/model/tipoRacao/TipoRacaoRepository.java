package br.csi.projeto_calculo_racao.model.tipoRacao;

import br.csi.projeto_calculo_racao.model.pet.Especie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TipoRacaoRepository extends JpaRepository<TipoRacao, Long> {
    Optional<TipoRacao> findByIdAndEspecie( Long id, Especie especie);
}
