package br.csi.projeto_calculo_racao.service;

import br.csi.projeto_calculo_racao.DTO.DadosCalculoDTO;
import br.csi.projeto_calculo_racao.model.calculo.Calculo;
import br.csi.projeto_calculo_racao.model.calculo.CalculoRepository;
import br.csi.projeto_calculo_racao.model.nutricao.caesAdultos.NutricaoCaesAdultos;
import br.csi.projeto_calculo_racao.model.nutricao.caesAdultos.NutricaoCaesAdultosRepository;
import br.csi.projeto_calculo_racao.model.nutricao.caesFilhotes.NutricaoCaesFilhotes;
import br.csi.projeto_calculo_racao.model.nutricao.caesFilhotes.NutricaoCaesFilhotesRepository;
import br.csi.projeto_calculo_racao.model.nutricao.nutricaoGatos.NutricaoGatos;
import br.csi.projeto_calculo_racao.model.nutricao.nutricaoGatos.NutricaoGatosRepository;
import br.csi.projeto_calculo_racao.model.pet.Especie;
import br.csi.projeto_calculo_racao.model.pet.Pet;
import br.csi.projeto_calculo_racao.model.pet.PetRepository;
import br.csi.projeto_calculo_racao.model.registroPeso.RegistroPeso;
import br.csi.projeto_calculo_racao.model.registroPeso.RegistroPesoRepository;
import br.csi.projeto_calculo_racao.model.tipoRacao.TipoRacao;
import br.csi.projeto_calculo_racao.model.tipoRacao.TipoRacaoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
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
    private final RegistroPesoRepository registroPesoRepository;

    public CalculoService( PetRepository petRepository,
                           TipoRacaoRepository tipoRacaoRepository,
                           CalculoRepository calculoRepository,
                           NutricaoCaesAdultosRepository nutricaoCaesAdultosRepository,
                           NutricaoCaesFilhotesRepository nutricaoCaesFilhotesRepository,
                           NutricaoGatosRepository nutricaoGatosRepository, RegistroPesoRepository registroPesoRepository ) {
        this.petRepository = petRepository;
        this.tipoRacaoRepository = tipoRacaoRepository;
        this.calculoRepository = calculoRepository;
        this.nutricaoCaesAdultosRepository = nutricaoCaesAdultosRepository;
        this.nutricaoCaesFilhotesRepository = nutricaoCaesFilhotesRepository;
        this.nutricaoGatosRepository = nutricaoGatosRepository;
        this.registroPesoRepository = registroPesoRepository;
    }

    // NEM adulto base (110 kcal/kg^0.75)
    private static final BigDecimal FATOR_NEM_ADULTO_BASE = new BigDecimal("110");

    //NEM para gatos adultos ativos (100 kcal/kg^0.67), usada como base para filhotes também
    private static final BigDecimal FATOR_NEM_ADULTO_GATO_BASE = new BigDecimal("100");

    public Calculo realizarCalculo ( UUID petUuid, DadosCalculoDTO dados ){

        Pet pet = petRepository.findByUuid ( petUuid )
                .orElseThrow ( () -> new RuntimeException ( "Pet não encontrado" ) );

        //logica para salvar o peso atual do pet no registro de peso
        try {
            RegistroPeso novoRegistroPeso = new RegistroPeso();
            novoRegistroPeso.setPet(pet);
            novoRegistroPeso.setPeso(dados.pesoAtual());
            novoRegistroPeso.setData_registro(LocalDate.now());
            registroPesoRepository.save(novoRegistroPeso);
            System.out.println("Registro de peso salvo automaticamente para o pet: " + pet.getNome());
        } catch (Exception e) {
            System.err.println("Erro ao salvar registro de peso automático: " + e.getMessage());
        }

        // lógica para determinar a ração e o valor de EM
        TipoRacao racao = null;
        BigDecimal emDaRacao;

        if (dados.idTipoRacao() != null && dados.emManual() != null) {
            throw new IllegalArgumentException("Forneça o tipo de ração OU o valor de EM manual, não ambos.");
        }

        if (dados.idTipoRacao() != null) {

            // Se o id foi fornecido, busca a ração no banco USANDO A ESPÉCIE DO PET
            racao = tipoRacaoRepository.findByIdAndEspecie(dados.idTipoRacao(), pet.getEspecie())
                    .orElseThrow(() -> new RuntimeException("Tipo de ração não encontrado para esta espécie de pet."));
            emDaRacao = racao.getEm();

        } else if (dados.emManual() != null) {
            // Se o valor manual foi fornecido, usa ele
            emDaRacao = dados.emManual();
            // Neste caso, o objeto 'racao' permanece nulo
        } else {
            throw new IllegalArgumentException("É obrigatório fornecer o tipo de ração ou o valor de EM manual.");
        }

        //calcular a idade do pet em anos e meses
        Period idade = Period.between ( pet.getData_nasc (), LocalDate.now () );
        int anos = idade.getYears ();
        long idadeTotalEmMeses = idade.toTotalMonths ();

        Calculo calculo = new Calculo ();
        calculo.setPet ( pet );
        calculo.setTipoRacao (racao);
        calculo.setPeso_atual ( dados.pesoAtual () );
        calculo.setIdade ( anos );
        calculo.setIdade_meses_total ( idadeTotalEmMeses );
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
                return calcularParaCaoFilhote(calculo, idadeTotalEmMeses);
            }
        } else if (pet.getEspecie() == Especie.GATO) {
            return calcularParaGato(calculo, anos, idadeTotalEmMeses);
        }
        // pode ter dado erro se chegar aqui (ex: espécie não suportada ou TD não implementado)
        throw new UnsupportedOperationException("Cálculo para esta espécie/fase ainda não implementado.");

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

    private Calculo calcularParaCaoFilhote(Calculo calculo, long idadeTotalEmMeses) {

        // fator de correção na tabela nutricao_caes_filhotes
        String porteAdultoStr = calculo.getPet().getPorte().name(); // Enum Porte para String

        NutricaoCaesFilhotes nutricaoFilhote = nutricaoCaesFilhotesRepository
                .findFatorCorrecao(porteAdultoStr, idadeTotalEmMeses)
                .orElseThrow(() -> new RuntimeException("Não foram encontrados fatores de correção para o porte e idade deste filhote."));

        BigDecimal fatorCorrecao = nutricaoFilhote.getFator_correcao();
        // Guardar o fator no cálculo
        calculo.setFator_correcao(fatorCorrecao);
        //sem coef
        calculo.setCoef_min(null);
        calculo.setCoef_max(null);

        // calcula o Peso Metabólico (PM)
        // Converte o peso atual (BigDecimal) para double
        double pesoAtualDouble = calculo.getPeso_atual().doubleValue();
        // Calcula a potência
        double pesoMetabolicoDouble = Math.pow(pesoAtualDouble, 0.75);
        // Converte o resultado de volta para BigDecimal
        BigDecimal pesoMetabolico = BigDecimal.valueOf(pesoMetabolicoDouble);

        //  NEM Adulto Base
        BigDecimal nemAdultoBase = FATOR_NEM_ADULTO_BASE.multiply(pesoMetabolico);

        // NEM do Filhote
        BigDecimal nemFilhote = fatorCorrecao.multiply(nemAdultoBase);
        calculo.setNem_media(nemFilhote.setScale(2, RoundingMode.HALF_UP));


        // filhote nao precisa de nem min e max
        calculo.setNem_min(calculo.getNem_media());
        calculo.setNem_max(calculo.getNem_media());
        calculo.setCoef_min(null);
        calculo.setCoef_max(null);


        // quantidade de ração em gramas
        BigDecimal emDaRacao = calculo.getEm();
        BigDecimal fatorKgParaGramas = new BigDecimal("1000");

        if (emDaRacao == null || emDaRacao.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor de EM da ração deve ser positivo.");
        }

        BigDecimal resultado = nemFilhote.divide(emDaRacao, 4, RoundingMode.HALF_UP)
                .multiply(fatorKgParaGramas)
                .setScale(2, RoundingMode.HALF_UP);

        calculo.setResultado(resultado);
        // Setar min/max do resultado igual ao resultado principal
        calculo.setResultado_min(resultado);
        calculo.setResultado_max(resultado);
        calculo.setData_calculo(LocalDateTime.now());

        return calculoRepository.save(calculo);
    }

    private Calculo calcularParaGato(Calculo calculo, int anos, long idadeTotalEmMeses) {

        BigDecimal pesoAtual = calculo.getPeso_atual();
        BigDecimal emDaRacao = calculo.getEm();
        BigDecimal fatorKgParaGramas = new BigDecimal("1000");

        // Validar EM da ração
        if (emDaRacao == null || emDaRacao.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor de EM da ração deve ser positivo e não nulo.");
        }

        // Peso Metabólico específico para Gatos (pesoAtual ^ 0.67)
        double pesoAtualDouble = calculo.getPeso_atual().doubleValue();
        double pesoMetabolicoGatoDouble = Math.pow(pesoAtualDouble, 0.67);
        BigDecimal pesoMetabolicoGato = BigDecimal.valueOf(pesoMetabolicoGatoDouble);

        BigDecimal nemMin;
        BigDecimal nemMax;
        BigDecimal nemMedia;

        // gato adulto
        if (anos >= 1) {
            calculo.setFase_vida("ADULTO_GATO");
            String nivelAtividadeGato = calcularNivelAtividadeGato(calculo.getNivel_atv()); // Converte a string do DTO

            // busca os coeficientes na tabela nutricao_gatos
            NutricaoGatos nutricaoGatoAdulto = nutricaoGatosRepository
                    .findAdultoByFaseVidaAndNivelAtv ("ADULTO", nivelAtividadeGato)
                    .orElseThrow(() -> new RuntimeException("Não foram encontrados coeficientes de nutrição para gato adulto com nível de atividade: " + nivelAtividadeGato));

            calculo.setCoef_min(nutricaoGatoAdulto.getCoef_min ());
            calculo.setCoef_max(nutricaoGatoAdulto.getCoef_max ());
            calculo.setFator_correcao(null); // fator de correção não se aplica a adultos

            // calculo da NEM
            nemMin = calculo.getCoef_min().multiply(pesoMetabolicoGato);
            nemMax = calculo.getCoef_max().multiply(pesoMetabolicoGato);
            nemMedia = nemMin.add(nemMax).divide(BigDecimal.valueOf(2), RoundingMode.HALF_UP);

        }
        // gatos filhotes
        else {
            calculo.setFase_vida("FILHOTE_GATO");

            // busca os dados nutricionais para filhote pela idade em meses
            NutricaoGatos nutricaoGatoFilhote = nutricaoGatosRepository
                    .findFilhoteByFase_vidaAndIdadeMeses ("FILHOTE", idadeTotalEmMeses)
                    .orElseThrow(() -> new RuntimeException("Não foram encontrados fatores de nutrição para gato filhote com idade de " + idadeTotalEmMeses + " meses."));

            BigDecimal fatorMin = nutricaoGatoFilhote.getCoef_min ();
            BigDecimal fatorMax = nutricaoGatoFilhote.getCoef_max ();

            calculo.setCoef_min(fatorMin);
            calculo.setCoef_max(fatorMax);
            calculo.setFator_correcao(null); // Fator de correção não se aplica

            // Calcula a NEM base de um gato adulto ativo (100 * PM)
            BigDecimal nemAdultoBaseGato = FATOR_NEM_ADULTO_GATO_BASE.multiply(pesoMetabolicoGato);

            // Calcula a NEM do filhote aplicando os fatores sobre a base de adulto
            nemMin = fatorMin.multiply(nemAdultoBaseGato);
            nemMax = fatorMax.multiply(nemAdultoBaseGato);
            nemMedia = nemMin.add(nemMax).divide(BigDecimal.valueOf(2), RoundingMode.HALF_UP);
        }

        // Salvar NEMs e Calcular Quantidade de Ração

        // Arredonda e salva os valores de NEM no objeto Calculo
        calculo.setNem_min(nemMin.setScale(2, RoundingMode.HALF_UP));
        calculo.setNem_max(nemMax.setScale(2, RoundingMode.HALF_UP));
        calculo.setNem_media(nemMedia.setScale(2, RoundingMode.HALF_UP));

        // Calcula a quantidade de ração em gramas
        BigDecimal resultadoMin = nemMin.divide(emDaRacao, 4, RoundingMode.HALF_UP)
                .multiply(fatorKgParaGramas)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal resultadoMax = nemMax.divide(emDaRacao, 4, RoundingMode.HALF_UP)
                .multiply(fatorKgParaGramas)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal resultadoMedia = nemMedia.divide(emDaRacao, 4, RoundingMode.HALF_UP)
                .multiply(fatorKgParaGramas)
                .setScale(2, RoundingMode.HALF_UP);

        calculo.setResultado_min(resultadoMin);
        calculo.setResultado_max(resultadoMax);
        calculo.setResultado(resultadoMedia);
        calculo.setData_calculo(LocalDateTime.now());

        return calculoRepository.save(calculo);
    }

    // Função auxiliar para mapear a string do DTO para os níveis da tabela nutricao_gatos
    private String calcularNivelAtividadeGato(String nivelAtividadeInput) {
        if (nivelAtividadeInput == null) {
            throw new IllegalArgumentException("Nível de atividade não pode ser nulo para gatos adultos.");
        }
        String nivelLower = nivelAtividadeInput.toLowerCase();

        // Mapeia termos comuns para os valores da tabela ('BAIXA', 'ATIVO')
        if (nivelLower.contains("baixa") || nivelLower.contains("castrado") || nivelLower.contains("pouca") || nivelLower.contains("inativo")) {
            return "BAIXA";
        } else if (nivelLower.contains("ativo") || nivelLower.contains("moderada") || nivelLower.contains("intensa")) {
            return "ATIVO";
        } else {
            // Se não reconhecer, lança erro
            throw new IllegalArgumentException("Nível de atividade não reconhecido para gato adulto: " + nivelAtividadeInput + ". Use termos como 'Baixa atividade' ou 'Ativo'.");
        }
    }

}

