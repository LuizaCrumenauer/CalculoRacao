package br.csi.projeto_calculo_racao.model.nutricao.caesFilhotes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface NutricaoCaesFilhotesRepository extends JpaRepository<NutricaoCaesFilhotes, Long> {
    // Query para encontrar o fator de correção baseado no porte (convertido para String)
    // e na idade total em meses.
    @Query("SELECT n FROM NutricaoCaesFilhotes n WHERE n.porte_adulto = :porteAdulto AND n.idade_meses_min <= :idadeMeses AND n.idade_meses_max >= :idadeMeses")
    Optional<NutricaoCaesFilhotes> findFatorCorrecao( String porteAdulto, long idadeMeses);
}
