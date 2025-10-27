package br.csi.projeto_calculo_racao.service;

import br.csi.projeto_calculo_racao.DTO.AdminCreateDTO;
import br.csi.projeto_calculo_racao.DTO.DadosAtualizacaoPerfilAdminDTO;
import br.csi.projeto_calculo_racao.model.tutor.Tutor;
import br.csi.projeto_calculo_racao.model.tutor.TutorRepository;
import br.csi.projeto_calculo_racao.model.usuario.Role;
import br.csi.projeto_calculo_racao.model.usuario.Usuario;
import br.csi.projeto_calculo_racao.model.usuario.UsuarioRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final TutorRepository tutorRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService( UsuarioRepository usuarioRepository, TutorRepository tutorRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.tutorRepository = tutorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario criarUsuario(Usuario usuario) {
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        return usuarioRepository.save(usuario);
    }

    public Usuario tornarAdmin(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new NoSuchElementException ("Usuário não encontrado com o ID: " + usuarioId)); // Usar NoSuchElementException

        if (usuario.getRole() == Role.ADMIN) {
            System.out.println("Usuário já é ADMIN: " + usuario.getEmail());
            return usuario;
        }

        usuario.setRole(Role.ADMIN);
        System.out.println("Usuário promovido a ADMIN: " + usuario.getEmail());
        return usuarioRepository.save(usuario);
    }

    public Usuario createAdmin( AdminCreateDTO dados) {
        if (this.usuarioRepository.findByEmail(dados.email()).isPresent()) {
            throw new DataIntegrityViolationException ("Este email já está em uso.");
        }
        Usuario novoAdmin = new Usuario();
        novoAdmin.setEmail(dados.email());
        novoAdmin.setSenha(this.passwordEncoder.encode(dados.senha()));
        novoAdmin.setRole(Role.ADMIN);
        return this.usuarioRepository.save(novoAdmin);
    }

    @Transactional
    public Usuario atualizarAdmin( DadosAtualizacaoPerfilAdminDTO dados) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailAtual = authentication.getName();

        Usuario adminParaAtualizar = usuarioRepository.findByEmail(emailAtual)
                .orElseThrow(() -> new RuntimeException("Usuário admin logado não encontrado no banco de dados."));


        if (adminParaAtualizar.getRole() != Role.ADMIN) {
            throw new org.springframework.security.access.AccessDeniedException("Apenas administradores podem atualizar suas próprias credenciais de admin.");
        }

        if (!passwordEncoder.matches(dados.senhaAtual(), adminParaAtualizar.getSenha())) {
            throw new BadCredentialsException ("Senha atual incorreta.");
        }

        boolean modificado = false;

        if (dados.novoEmail() != null && !dados.novoEmail().isBlank() && !dados.novoEmail().equalsIgnoreCase(emailAtual)) {
            Optional<Usuario> usuarioComNovoEmail = usuarioRepository.findByEmail(dados.novoEmail());
            if (usuarioComNovoEmail.isPresent() && !usuarioComNovoEmail.get().getId().equals(adminParaAtualizar.getId())) {
                throw new DataIntegrityViolationException("O novo email fornecido já está em uso por outra conta.");
            }
            adminParaAtualizar.setEmail(dados.novoEmail());
            modificado = true;
        }

        if (dados.novaSenha() != null && !dados.novaSenha().isBlank()) {
            adminParaAtualizar.setSenha(passwordEncoder.encode(dados.novaSenha()));
            modificado = true;
        }

        if (modificado) {
            return usuarioRepository.save(adminParaAtualizar);
        } else {
            return adminParaAtualizar;
        }
    }

    @Transactional
    public void excluirUsuario(Long usuarioId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String adminLogadoEmail = authentication.getName();

        Usuario usuarioParaExcluir = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new NoSuchElementException("Usuário não encontrado com o ID: " + usuarioId));

        if (usuarioParaExcluir.getEmail().equalsIgnoreCase(adminLogadoEmail)) {
            throw new IllegalArgumentException("Um administrador não pode excluir a própria conta através deste endpoint.");
        }

        if (usuarioParaExcluir.getRole() == Role.USER) {
            Tutor tutorAssociado = tutorRepository.findByUsuario_Email(usuarioParaExcluir.getEmail())
                    .orElseThrow(() -> new NoSuchElementException("Perfil de Tutor não encontrado para o usuário com email: " + usuarioParaExcluir.getEmail() + ". Exclusão do usuário cancelada."));

            tutorRepository.delete(tutorAssociado);
            System.out.println("Tutor (e usuário associado) excluído com sucesso: " + usuarioParaExcluir.getEmail());

        } else if (usuarioParaExcluir.getRole() == Role.ADMIN) { //
            usuarioRepository.delete(usuarioParaExcluir);
            System.out.println("Usuário Admin excluído com sucesso: " + usuarioParaExcluir.getEmail());
        } else {
            throw new IllegalStateException("Role de usuário desconhecida: " + usuarioParaExcluir.getRole());
        }
    }

    @Transactional
    public void excluirPropriaConta() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailUsuarioLogado = authentication.getName();

        Tutor tutorParaExcluir = tutorRepository.findByUsuario_Email(emailUsuarioLogado)
                .orElseThrow(() -> new NoSuchElementException("Perfil de Tutor não encontrado para o usuário logado. A exclusão só é permitida para Tutores."));

        if (tutorParaExcluir.getUsuario().getRole() != Role.USER) {
            throw new AccessDeniedException ("Apenas usuários com perfil de Tutor podem excluir a própria conta por este endpoint.");
        }

        tutorRepository.delete(tutorParaExcluir);
        System.out.println("Tutor (e usuário associado) auto-excluído com sucesso: " + emailUsuarioLogado);
    }
}
