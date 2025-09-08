package br.csi.projeto_calculo_racao.service;

import br.csi.projeto_calculo_racao.model.tutor.Tutor;
import br.csi.projeto_calculo_racao.model.tutor.TutorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TutorService {

    private final TutorRepository repository;

    public TutorService(TutorRepository repository) {
        this.repository = repository;
    }


    public void salvar(Tutor tutor) {
        this.repository.save(tutor);
    }

    public List<Tutor> listarTutores() {
        return this.repository.findAll();
    }

    public Tutor getTutor(Long id) {
        return this.repository.findById(id).get();
    }

    public void excluir(Long id) {
        this.repository.deleteById(id);
    }

    public void atualizar(Tutor tutor) {

        Tutor tutorExistente = this.repository.getReferenceById(tutor.getId ());

        tutorExistente.setNome(tutor.getNome ());
        tutorExistente.setEmail(tutor.getEmail ());
        tutorExistente.setTelefone(tutor.getTelefone ());
        tutorExistente.setEndereco(tutor.getEndereco ());

        this.repository.save(tutorExistente);
    }
}
