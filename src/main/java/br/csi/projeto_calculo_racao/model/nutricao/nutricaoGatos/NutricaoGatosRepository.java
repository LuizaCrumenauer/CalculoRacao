package br.csi.projeto_calculo_racao.model.nutricao.nutricaoGatos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface NutricaoGatosRepository extends JpaRepository<NutricaoGatos, Long> {

    // Query explícita para Gatos Adultos: busca por fase_vida e nivel_atv
    @Query("SELECT n FROM NutricaoGatos n WHERE n.fase_vida = :faseVida AND n.nivel_atv = :nivelAtv")
    Optional<NutricaoGatos> findAdultoByFaseVidaAndNivelAtv(String faseVida, String nivelAtv);

    // Query para filhotes (já estava correta)
    @Query("SELECT n FROM NutricaoGatos n WHERE n.fase_vida = :faseVida AND :idadeMeses BETWEEN n.idade_meses_min AND n.idade_meses_max")
    Optional<NutricaoGatos> findFilhoteByFase_vidaAndIdadeMeses(String faseVida, long idadeMeses);

}
