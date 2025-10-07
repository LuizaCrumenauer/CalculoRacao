package br.csi.projeto_calculo_racao.service;

import br.csi.projeto_calculo_racao.DTO.DadosCalculoDTO;
import br.csi.projeto_calculo_racao.model.calculo.Calculo;
import br.csi.projeto_calculo_racao.model.calculo.CalculoRepository;
import br.csi.projeto_calculo_racao.model.nutricao.caesAdultos.NutricaoCaesAdultos;
import br.csi.projeto_calculo_racao.model.nutricao.caesAdultos.NutricaoCaesAdultosRepository;
import br.csi.projeto_calculo_racao.model.nutricao.caesFilhotes.NutricaoCaesFilhotesRepository;
import br.csi.projeto_calculo_racao.model.nutricao.nutricaoGatos.NutricaoGatosRepository;
import br.csi.projeto_calculo_racao.model.pet.Especie;
import br.csi.projeto_calculo_racao.model.pet.Pet;
import br.csi.projeto_calculo_racao.model.pet.PetRepository;
import br.csi.projeto_calculo_racao.model.tipoRacao.TipoRacao;
import br.csi.projeto_calculo_racao.model.tipoRacao.TipoRacaoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.UUID;

@Service
public class CalculoService {

    private final PetRepository petRepository;
    private final TipoRacaoRepository tipoRacaoRepository;
    private final CalculoRepository calculoRepository;
    private final NutricaoCaesAdultosRepository nutricaoCaesAdultosRepository;
    private final NutricaoCaesFilhotesRepository nutricaoCaesFilhotesRepository;
    private final NutricaoGatosRepository nutricaoGatosRepository;

    public CalculoService(PetRepository petRepository,
                          TipoRacaoRepository tipoRacaoRepository,
                          CalculoRepository calculoRepository,
                          NutricaoCaesAdultosRepository nutricaoCaesAdultosRepository,
                          NutricaoCaesFilhotesRepository nutricaoCaesFilhotesRepository,
                          NutricaoGatosRepository nutricaoGatosRepository) {
        this.petRepository = petRepository;
        this.tipoRacaoRepository = tipoRacaoRepository;
        this.calculoRepository = calculoRepository;
        this.nutricaoCaesAdultosRepository = nutricaoCaesAdultosRepository;
        this.nutricaoCaesFilhotesRepository = nutricaoCaesFilhotesRepository;
        this.nutricaoGatosRepository = nutricaoGatosRepository;
    }

    public Calculo realizarCalculo ( UUID petUuid, DadosCalculoDTO dados ){

        Pet pet = petRepository.findByUuid ( petUuid )
                .orElseThrow ( () -> new RuntimeException ( "Pet não encontrado" ) );

        // 2. Lógica para determinar a ração e o valor de EM
        TipoRacao racao = null;
        BigDecimal emDaRacao;

        // Validação de entrada
        if (dados.idTipoRacao() != null && dados.emManual() != null) {
            throw new IllegalArgumentException("Forneça o tipo de ração OU o valor de EM manual, não ambos.");
        }

        if (dados.idTipoRacao() != null) {
            // Se o ID foi fornecido, busca a ração no banco
            racao = tipoRacaoRepository.findById(dados.idTipoRacao())
                    .orElseThrow(() -> new RuntimeException("Tipo de ração não encontrado"));
            emDaRacao = racao.getEm();
        } else if (dados.emManual() != null) {
            // Se o valor manual foi fornecido, usa ele
            emDaRacao = dados.emManual();
            // Neste caso, o objeto 'racao' permanece nulo
        } else {
            throw new IllegalArgumentException("É obrigatório fornecer o tipo de ração ou o valor de EM manual.");
        }


        System.out.println("----------- DEBUG DA IDADE -----------");
        System.out.println("Data de Nascimento do Pet: " + pet.getData_nasc());
        System.out.println("Data Atual (LocalDate.now()): " + LocalDate.now());

        //calcular a idade do pet em anos e meses
        Period idade = Period.between ( pet.getData_nasc (), LocalDate.now () );
        int anos = idade.getYears ();
        int meses = idade.getMonths ();
        long idadeTotalEmMeses = idade.toTotalMonths ();

        Calculo calculo = new Calculo ();
        calculo.setPet ( pet );
        calculo.setTipoRacao (racao);
        calculo.setPeso_atual ( dados.pesoAtual () );
        calculo.setIdade ( anos );
        calculo.setNivel_atv ( dados.nivelAtividade () );
        calculo.setEm(emDaRacao);

        System.out.println("Anos calculados: " + anos);
        System.out.println("------------------------------------");

        if (pet.getEspecie() == Especie.CACHORRO) {

            if (anos >= 1) {
                calculo.setFase_vida("ADULTO");
                return calcularParaCaoAdulto(calculo);
            } else {
                calculo.setFase_vida("FILHOTE");
                // TODO: Implementar calcularParaCaoFilhote(calculo);
            }
        } else if (pet.getEspecie() == Especie.GATO) {
            // TODO: Implementar a lógica para gatos
        }

        return null; //mudar


    }

    private Calculo calcularParaCaoAdulto ( Calculo calculo ) {

        NutricaoCaesAdultos nutricao = nutricaoCaesAdultosRepository
                .findByFiltros ( calculo.getIdade (), calculo.getNivel_atv () )
                .orElseThrow (() -> new RuntimeException ("Não foram encontrados coeficientes de nutrição para a idade e nível de atividade fornecidos.\""));

        calculo.setCoef_min ( nutricao.getCoef_min () );
        calculo.setCoef_max ( nutricao.getCoef_max() );

        //pesoMetabolico (peso ^ 0.75)
        double pesoMetabolico = Math.pow ( calculo.getPeso_atual ().doubleValue (), 0.75 );

        //NEM
        BigDecimal nemMin = nutricao.getCoef_min ().multiply ( BigDecimal.valueOf ( pesoMetabolico ) );
        BigDecimal nemMax = nutricao.getCoef_max ().multiply ( BigDecimal.valueOf ( pesoMetabolico ) );
        BigDecimal nemMedia = nemMin.add(nemMax).divide ( BigDecimal.valueOf ( 2 ) );

        calculo.setNem_min ( nemMin );
        calculo.setNem_max ( nemMax );
        calculo.setNem_media ( nemMedia );

        //qnt de racao em gramas
        BigDecimal fatorKgParaGramas = new BigDecimal("1000");
        BigDecimal resultadoMin = nemMin.divide(calculo.getEm(), 4, RoundingMode.HALF_UP).multiply(fatorKgParaGramas).setScale(2, RoundingMode.HALF_UP);
        BigDecimal resultadoMax = nemMax.divide(calculo.getEm(), 4, RoundingMode.HALF_UP).multiply(fatorKgParaGramas).setScale(2, RoundingMode.HALF_UP);
        BigDecimal resultadoMedia = nemMedia.divide(calculo.getEm(), 4, RoundingMode.HALF_UP).multiply(fatorKgParaGramas).setScale(2, RoundingMode.HALF_UP);

        calculo.setResultado_min ( resultadoMin );
        calculo.setResultado_max ( resultadoMax );
        calculo.setResultado (resultadoMedia);
        calculo.setData_calculo ( LocalDateTime.now () );

        return calculoRepository.save( calculo );
    }
}
