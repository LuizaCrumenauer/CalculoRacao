package br.csi.projeto_calculo_racao.service;

import br.csi.projeto_calculo_racao.DTO.DadosAtualizacaoPesoDTO; // <-- Importe o novo DTO
import br.csi.projeto_calculo_racao.DTO.DadosRegistroPesoDTO;
import br.csi.projeto_calculo_racao.model.pet.Pet;
import br.csi.projeto_calculo_racao.model.pet.PetRepository;
import br.csi.projeto_calculo_racao.model.registroPeso.RegistroPeso;
import br.csi.projeto_calculo_racao.model.registroPeso.RegistroPesoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class RegistroPesoService {

    private final RegistroPesoRepository registroPesoRepository;
    private final PetRepository petRepository;

    public RegistroPesoService(RegistroPesoRepository registroPesoRepository, PetRepository petRepository) {
        this.registroPesoRepository = registroPesoRepository;
        this.petRepository = petRepository;
    }

    public void registrarNovoPeso(UUID petUuid, DadosRegistroPesoDTO dados) {
        Pet pet = petRepository.findByUuid(petUuid)
                .orElseThrow(() -> new RuntimeException("Pet não encontrado"));

        RegistroPeso novoRegistro = new RegistroPeso();
        novoRegistro.setPet(pet);
        novoRegistro.setPeso(dados.peso());
        novoRegistro.setData_registro(LocalDate.now());

        registroPesoRepository.save(novoRegistro);
    }

    public List<RegistroPeso> historicoDePesoPorPet(UUID petUuid) {
        Pet pet = petRepository.findByUuid(petUuid)
                .orElseThrow(() -> new RuntimeException("Pet não encontrado"));
        return pet.getHistoricoPeso();
    }

    // --- NOVO MÉTODO PARA ATUALIZAR ---
    public RegistroPeso atualizar(Long idRegistro, DadosAtualizacaoPesoDTO dados) {
        // 1. Busca o registro de peso que queremos editar
        RegistroPeso registroExistente = registroPesoRepository.findById(idRegistro)
                .orElseThrow(() -> new RuntimeException("Registro de peso não encontrado."));

        // 2. Atualiza os dados do registro com as informações do DTO
        registroExistente.setPeso(dados.peso());
        registroExistente.setData_registro(dados.data_registro());

        // 3. Salva as alterações (o JPA fará um UPDATE)
        return registroPesoRepository.save(registroExistente);
    }

    // --- NOVO MÉTODO PARA EXCLUIR ---
    public void excluir(Long idRegistro) {
        // 1. Verifica se o registro existe antes de tentar deletar
        if (!registroPesoRepository.existsById(idRegistro)) {
            throw new RuntimeException("Registro de peso não encontrado.");
        }
        // 2. Exclui o registro
        registroPesoRepository.deleteById(idRegistro);
    }
}
