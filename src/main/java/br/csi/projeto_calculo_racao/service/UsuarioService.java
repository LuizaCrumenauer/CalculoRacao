package br.csi.projeto_calculo_racao.service;

import br.csi.projeto_calculo_racao.DTO.AdminCreateDTO;
import br.csi.projeto_calculo_racao.DTO.DadosAtualizacaoPerfilAdminDTO;
import br.csi.projeto_calculo_racao.model.usuario.Role;
import br.csi.projeto_calculo_racao.model.usuario.Usuario;
import br.csi.projeto_calculo_racao.model.usuario.UsuarioRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario criarUsuario(Usuario usuario) {
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        return usuarioRepository.save(usuario);
    }

    public Usuario tornarAdmin(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        usuario.setRole(Role.ADMIN);

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
}
