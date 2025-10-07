package br.csi.projeto_calculo_racao.model.nutricao.caesAdultos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface NutricaoCaesAdultosRepository extends JpaRepository<NutricaoCaesAdultos, Long> {
    @Query("SELECT n FROM NutricaoCaesAdultos n WHERE n.idade_min_anos <= :idadeEmAnos AND n.idade_max_anos >= :idadeEmAnos AND n.nivel_atv = :nivelAtividade")
    Optional<NutricaoCaesAdultos>findByFiltros(int idadeEmAnos, String nivelAtividade);
}
