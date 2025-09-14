package br.csi.projeto_calculo_racao.service;

import br.csi.projeto_calculo_racao.model.pet.Pet;
import br.csi.projeto_calculo_racao.model.pet.PetRepository;
import br.csi.projeto_calculo_racao.model.tutor.Tutor;
import br.csi.projeto_calculo_racao.model.tutor.TutorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PetService {

    private final PetRepository petRepository;
    private final TutorRepository tutorRepository;

    public PetService(PetRepository petRepository, TutorRepository tutorRepository) {
        this.petRepository = petRepository;
        this.tutorRepository = tutorRepository;
    }

    public void cadastar(Pet pet, UUID uuidTutor) {
        Tutor tutor = tutorRepository.findByUuid ( uuidTutor ).orElseThrow( () -> new RuntimeException ("Tutor não encontrado"));
        pet.setTutor(tutor);
        this.petRepository.save(pet);
    }

    public List<Pet> listarPets() {
        return petRepository.findAll();
    }

    public Pet buscarPet( UUID uuid ) {
        return this.petRepository.findByUuid (uuid)
                .orElseThrow (() -> new RuntimeException ("Pet não encontrado"));
    }

    public void atualizar(Pet petAtualizado, UUID uuidTutor) {
        Tutor novoTutor = this.tutorRepository.findByUuid ( uuidTutor )
                .orElseThrow (() -> new RuntimeException ("Tutor não encontrado"));
        Pet petExistente = this.petRepository.findByUuid (petAtualizado.getUuid ())
                .orElseThrow(() -> new RuntimeException("Pet não encontrado para a atualização."));

        petExistente.setNome(petAtualizado.getNome());
        petExistente.setEspecie(petAtualizado.getEspecie());
        petExistente.setPorte(petAtualizado.getPorte());
        petExistente.setData_nasc(petAtualizado.getData_nasc());
        petExistente.setSexo(petAtualizado.getSexo());
        petExistente.setTutor(novoTutor);

        this.petRepository.save(petExistente);
    }

    public void excluir(UUID uuid) {
        Pet pet = this.buscarPet (uuid);
        this.petRepository.deleteById(pet.getId ());
    }


}
