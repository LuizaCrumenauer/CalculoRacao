package br.csi.projeto_calculo_racao.service;

import br.csi.projeto_calculo_racao.DTO.DadosCadastroTutorDTO;
import br.csi.projeto_calculo_racao.model.tutor.Tutor;
import br.csi.projeto_calculo_racao.model.tutor.TutorRepository;
import br.csi.projeto_calculo_racao.model.usuario.Role;
import br.csi.projeto_calculo_racao.model.usuario.Usuario;
import br.csi.projeto_calculo_racao.model.usuario.UsuarioRepository;
import br.csi.projeto_calculo_racao.util.CpfUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class TutorService {

    private final TutorRepository repository;
    private final UsuarioService usuarioService; // Injetamos o UsuarioService
    private final UsuarioRepository usuarioRepository;

    public TutorService( TutorRepository repository, UsuarioService usuarioService, UsuarioRepository usuarioRepository ) {
        this.repository = repository;
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;

    }

    @Transactional
    public Tutor cadastrarTutor(DadosCadastroTutorDTO dados) {
        // Validações para evitar duplicação de CPF e Email
        if (this.repository.findByCpf(CpfUtils.limpar(dados.cpf())).isPresent()) {
            throw new DataIntegrityViolationException("CPF já cadastrado no sistema.");
        }
        if (this.usuarioRepository.findByEmail(dados.email()).isPresent()) {
            throw new DataIntegrityViolationException("Email já cadastrado no sistema.");
        }

        // 1. Cria o objeto Usuario
        Usuario novoUsuario = new Usuario();
        novoUsuario.setEmail(dados.email());
        novoUsuario.setSenha(dados.senha());
        novoUsuario.setRole(Role.USER); // Por padrão, td novo tutor é um USER

        // 2. Salva o usuário usando o UsuarioService (que já criptografa a senha)
        Usuario usuarioSalvo = usuarioService.criarUsuario(novoUsuario);

        // 3. Cria o objeto Tutor e associa o usuário salvo
        Tutor novoTutor = new Tutor();
        novoTutor.setNome(dados.nome());
        novoTutor.setCpf(dados.cpf());
        novoTutor.setTelefone(dados.telefone());
        novoTutor.setEndereco(dados.endereco());
        novoTutor.setUsuario(usuarioSalvo); // Associa o usuário ao tutor

        // 4. Salva o tutor
        return this.repository.save(novoTutor);
    }

    public Tutor buscarTutorPorEmail(String email) {
        return this.repository.findByUsuario_Email(email)
                .orElseThrow(() -> new NoSuchElementException("Perfil de tutor não encontrado para o usuário logado."));
    }


    public List<Tutor> listarTutores() {
        return this.repository.findAll();
    }

    public Tutor buscarTutor(UUID uuid) {
        return this.repository.findByUuid(uuid)
                .orElseThrow(() -> new NoSuchElementException("Tutor não encontrado"));
    }

    public void excluir(UUID uuid) {
        Tutor tutor = this.buscarTutor(uuid);
        this.repository.deleteById(tutor.getId());
    }

    public void atualizar(Tutor tutor) {

        Tutor tutorExistente = this.repository.getReferenceById(tutor.getId());

        tutorExistente.setNome(tutor.getNome());
        tutorExistente.setTelefone(tutor.getTelefone());
        tutorExistente.setEndereco(tutor.getEndereco());

        this.repository.save(tutorExistente);
    }
}
