package br.csi.projeto_calculo_racao.service;

import br.csi.projeto_calculo_racao.DTO.DadosAtualizacaoPerfilTutorDTO;
import br.csi.projeto_calculo_racao.DTO.DadosCadastroTutorDTO;
import br.csi.projeto_calculo_racao.model.tutor.Tutor;
import br.csi.projeto_calculo_racao.model.tutor.TutorRepository;
import br.csi.projeto_calculo_racao.model.usuario.Role;
import br.csi.projeto_calculo_racao.model.usuario.Usuario;
import br.csi.projeto_calculo_racao.model.usuario.UsuarioRepository;
import br.csi.projeto_calculo_racao.util.CpfUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
public class TutorService {

    private final TutorRepository repository;
    private final UsuarioService usuarioService; // Injetamos o UsuarioService
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public TutorService( TutorRepository repository, UsuarioService usuarioService, UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder ) {
        this.repository = repository;
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
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

        Usuario novoUsuario = new Usuario();
        novoUsuario.setEmail(dados.email());
        novoUsuario.setSenha(dados.senha());
        novoUsuario.setRole(Role.USER); // Por padrão, td novo tutor é um USER

        Usuario usuarioSalvo = usuarioService.criarUsuario(novoUsuario);

        Tutor novoTutor = new Tutor();
        novoTutor.setNome(dados.nome());
        novoTutor.setCpf(dados.cpf());
        novoTutor.setTelefone(dados.telefone());
        novoTutor.setEndereco(dados.endereco());
        novoTutor.setUsuario(usuarioSalvo);

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

    @Transactional
    public Tutor atualizarPerfilCompleto(DadosAtualizacaoPerfilTutorDTO dados) {
        //identifica ususario pelo token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailAtual = authentication.getName();
        Tutor tutorParaAtualizar = this.repository.findByUsuario_Email(emailAtual)
                .orElseThrow(() -> new NoSuchElementException("Perfil de tutor não encontrado para o usuário logado."));
        Usuario usuarioAssociado = tutorParaAtualizar.getUsuario();

        if (dados.nome() != null && !dados.nome().isBlank()) {
            tutorParaAtualizar.setNome(dados.nome());
        }
        if (dados.telefone() != null) {
            tutorParaAtualizar.setTelefone(dados.telefone());
        }
        if (dados.endereco() != null) {
            tutorParaAtualizar.setEndereco(dados.endereco());
        }

        if (dados.novoCpf() != null && !dados.novoCpf().isBlank()) {
            String novoCpfLimpo = CpfUtils.limpar(dados.novoCpf());
            String cpfAtualLimpo = CpfUtils.limpar(tutorParaAtualizar.getCpf());

            if (!novoCpfLimpo.equals(cpfAtualLimpo)) {
                Optional<Tutor> tutorComNovoCpf = repository.findByCpf(novoCpfLimpo);
                if (tutorComNovoCpf.isPresent() && !tutorComNovoCpf.get().getId().equals(tutorParaAtualizar.getId())) {
                    throw new DataIntegrityViolationException("O novo CPF fornecido já está cadastrado em outra conta.");
                }
                tutorParaAtualizar.setCpf(novoCpfLimpo);
            }
        }

        boolean querMudarEmail = dados.novoEmail() != null && !dados.novoEmail().isBlank() && !dados.novoEmail().equalsIgnoreCase(emailAtual);
        boolean querMudarSenha = dados.novaSenha() != null && !dados.novaSenha().isBlank();

        if (querMudarEmail || querMudarSenha) {
            if (dados.senhaAtual() == null || dados.senhaAtual().isBlank()) {
                throw new BadCredentialsException("A senha atual é obrigatória para alterar o email ou a senha.");
            }

            if (!passwordEncoder.matches(dados.senhaAtual(), usuarioAssociado.getSenha())) {
                throw new BadCredentialsException("Senha atual incorreta.");
            }

        }

        if (querMudarEmail) {

            Optional<Usuario> usuarioComNovoEmail = usuarioRepository.findByEmail(dados.novoEmail());

            if (usuarioComNovoEmail.isPresent() && !usuarioComNovoEmail.get().getId().equals(usuarioAssociado.getId())) {
                throw new DataIntegrityViolationException("O novo email fornecido já está em uso por outra conta.");
            }

            usuarioAssociado.setEmail(dados.novoEmail());
            System.out.println("Email do usuário atualizado para: " + dados.novoEmail());
        }

        if (querMudarSenha) {

            if (passwordEncoder.matches(dados.novaSenha(), usuarioAssociado.getSenha())) {
                throw new BadCredentialsException("A nova senha não pode ser igual à senha atual.");
            }

            usuarioAssociado.setSenha(passwordEncoder.encode(dados.novaSenha()));
            System.out.println("Senha do usuário atualizada.");
        }

        Tutor tutorSalvo = this.repository.save(tutorParaAtualizar);

        return tutorSalvo;
    }
}
