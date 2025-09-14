package br.csi.projeto_calculo_racao.service;

import br.csi.projeto_calculo_racao.model.tutor.Tutor;
import br.csi.projeto_calculo_racao.model.tutor.TutorRepository;
import br.csi.projeto_calculo_racao.util.CpfUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class TutorService {

    private final TutorRepository repository;

    public TutorService(TutorRepository repository) {
        this.repository = repository;
    }


    public void salvar(Tutor tutor) {
        if (this.repository.findByCpf ( CpfUtils.limpar ( tutor.getCpf ())).isPresent ()){
            throw new DataIntegrityViolationException ( "CPF já cadastrado no sistema." );
        }
        if ( this.repository.findByEmail ( tutor.getEmail ()).isPresent () ){
            throw new DataIntegrityViolationException("Email já cadastrado no sistema.");
        }
        this.repository.save(tutor);
    }

    public List<Tutor> listarTutores() {
        return this.repository.findAll();
    }

    public Tutor buscarTutor( UUID uuid ) {
        return this.repository.findByUuid (uuid)
                .orElseThrow (() -> new NoSuchElementException ("Tutor não encontrado") );
    }

    public void excluir(UUID uuid) {
        Tutor tutor = this.buscarTutor(uuid);
        this.repository.deleteById(tutor.getId ());
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
