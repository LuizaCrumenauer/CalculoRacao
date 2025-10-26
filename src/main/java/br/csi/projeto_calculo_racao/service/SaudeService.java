package br.csi.projeto_calculo_racao.service;

import br.csi.projeto_calculo_racao.DTO.ItemSaudeDTO;
import br.csi.projeto_calculo_racao.DTO.RegistroSaudeDTO;
import br.csi.projeto_calculo_racao.model.pet.Pet;
import br.csi.projeto_calculo_racao.model.pet.PetRepository;
import br.csi.projeto_calculo_racao.model.registroSaude.ItemSaude;
import br.csi.projeto_calculo_racao.model.registroSaude.ItemSaudeRepository;
import br.csi.projeto_calculo_racao.model.registroSaude.RegistroSaude;
import br.csi.projeto_calculo_racao.model.registroSaude.RegistroSaudeRepository;
import br.csi.projeto_calculo_racao.model.tutor.Tutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class SaudeService {

    private final ItemSaudeRepository itemSaudeRepository;
    private final RegistroSaudeRepository registroSaudeRepository;
    private final PetRepository petRepository;

    public SaudeService(ItemSaudeRepository itemSaudeRepository, RegistroSaudeRepository registroSaudeRepository, PetRepository petRepository) {
        this.itemSaudeRepository = itemSaudeRepository;
        this.registroSaudeRepository = registroSaudeRepository;
        this.petRepository = petRepository;
    }

    public List<ItemSaude> getItensDisponiveis(Tutor tutor) {
        return itemSaudeRepository.findGlobalAndTutorItems(tutor.getId());
    }

    // Cria um item de saúde. Se o tutor for nulo, é um item de Admin.
    @Transactional
    public ItemSaude criarItem(ItemSaudeDTO dto, Tutor tutor) {
        ItemSaude novoItem = new ItemSaude();
        novoItem.setNome(dto.nome());
        novoItem.setTipo(dto.tipo());
        novoItem.setTutor(tutor);
        return itemSaudeRepository.save(novoItem);
    }

    @Transactional
    public RegistroSaude registrarSaude(RegistroSaudeDTO dto) {
        Pet pet = petRepository.findByUuid(dto.petUuid())
                .orElseThrow(() -> new RuntimeException("Pet não encontrado."));
        ItemSaude item = itemSaudeRepository.findById(dto.itemSaudeId())
                .orElseThrow(() -> new RuntimeException("Item de saúde não encontrado."));

        RegistroSaude novoRegistro = new RegistroSaude();
        novoRegistro.setPet(pet);
        novoRegistro.setItemSaude(item);
        novoRegistro.setData_aplicacao(dto.dataAplicacao());
        novoRegistro.setProxima_dose(dto.proximaDose());

        return registroSaudeRepository.save(novoRegistro);
    }

    public List<RegistroSaude> getRegistrosPorPet(UUID petUuid) {
        Pet pet = petRepository.findByUuid(petUuid)
                .orElseThrow(() -> new RuntimeException("Pet não encontrado."));
        return pet.getRegistrosSaude();
    }

    //para admin
    public List<ItemSaude> getAllItens() {
        return itemSaudeRepository.findAll();
    }
}
