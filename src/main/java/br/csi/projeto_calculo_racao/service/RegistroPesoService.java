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
        if (dados.data() != null) {
            novoRegistro.setData_registro(dados.data());
        } else {
            novoRegistro.setData_registro(LocalDate.now());
        }

        registroPesoRepository.save(novoRegistro);

    }

    public List<RegistroPeso> historicoDePesoPorPet(UUID petUuid) {
        Pet pet = petRepository.findByUuid(petUuid)
                .orElseThrow(() -> new RuntimeException("Pet não encontrado"));
        return pet.getHistoricoPeso();
    }

    public RegistroPeso atualizar(Long idRegistro, DadosAtualizacaoPesoDTO dados) {
        RegistroPeso registroExistente = registroPesoRepository.findById(idRegistro)
                .orElseThrow(() -> new RuntimeException("Registro de peso não encontrado."));

        registroExistente.setPeso(dados.peso());
        registroExistente.setData_registro(dados.data_registro());

        return registroPesoRepository.save(registroExistente);
    }

    public void excluir(Long idRegistro) {
        if (!registroPesoRepository.existsById(idRegistro)) {
            throw new RuntimeException("Registro de peso não encontrado.");
        }
        registroPesoRepository.deleteById(idRegistro);
    }
}
