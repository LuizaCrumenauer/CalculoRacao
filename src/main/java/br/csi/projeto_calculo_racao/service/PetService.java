package br.csi.projeto_calculo_racao.service;

import br.csi.projeto_calculo_racao.model.pet.Pet;
import br.csi.projeto_calculo_racao.model.pet.PetRepository;
import br.csi.projeto_calculo_racao.model.tutor.Tutor;
import br.csi.projeto_calculo_racao.model.tutor.TutorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PetService {

    private final PetRepository petRepository;
    private final TutorRepository tutorRepository;

    public PetService(PetRepository petRepository, TutorRepository tutorRepository) {
        this.petRepository = petRepository;
        this.tutorRepository = tutorRepository;
    }

    public void cadastar(Pet pet, Long tutorId) {
        Tutor tutor = tutorRepository.findById(tutorId).orElseThrow( () -> new RuntimeException ("Tutor não encontrado"));
        pet.setTutor(tutor);
        this.petRepository.save(pet);
    }

    public List<Pet> listarPets() {
        return petRepository.findAll();
    }

    public Pet buscarPetPorId(Long id) {
        return this.petRepository.findById ( id ).orElseThrow (() -> new RuntimeException ("Pet não encontrado"));
    }

    public void atualizar(Pet pet, Long tutorId) {
        Tutor novoTutor = this.tutorRepository.findById ( tutorId ).orElseThrow (() -> new RuntimeException ("Tutor não encontrado"));
        Pet petExistente = this.petRepository.findById(pet.getId()).orElseThrow(() -> new RuntimeException("Pet não encontrado para a atualização."));

        petExistente.setNome(pet.getNome());
        petExistente.setEspecie(pet.getEspecie());
        petExistente.setPorte(pet.getPorte());
        petExistente.setData_nasc(pet.getData_nasc());
        petExistente.setTutor(novoTutor);
    }

    public void excluir(Long id) {
        this.petRepository.deleteById(id);
    }


}
