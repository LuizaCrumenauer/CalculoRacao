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
        // Codifica a senha antes de salvar
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        return usuarioRepository.save(usuario);
    }

    public Usuario tornarAdmin(Long usuarioId) {
        // Busca o usuário ou lança uma exceção se não encontrar
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        // Atribui o papel de ADMIN
        usuario.setRole(Role.ADMIN);

        // Salva a alteração
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
        // 1. Pega o usuário (admin) atualmente autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailAtual = authentication.getName();

        Usuario adminParaAtualizar = usuarioRepository.findByEmail(emailAtual)
                .orElseThrow(() -> new RuntimeException("Usuário admin logado não encontrado no banco de dados."));

        // Garante que apenas um ADMIN pode usar esta lógica (segurança extra)
        // Embora o endpoint possa ser protegido, é bom ter a verificação no serviço.
        if (adminParaAtualizar.getRole() != Role.ADMIN) {
            throw new org.springframework.security.access.AccessDeniedException("Apenas administradores podem atualizar suas próprias credenciais de admin.");
        }

        // 2. Verifica se a senha atual fornecida está correta (OBRIGATÓRIO)
        if (!passwordEncoder.matches(dados.senhaAtual(), adminParaAtualizar.getSenha())) {
            throw new BadCredentialsException ("Senha atual incorreta.");
        }

        boolean modificado = false; // Flag para saber se houve alteração

        // 3. Atualiza o Email (se fornecido E diferente do atual)
        if (dados.novoEmail() != null && !dados.novoEmail().isBlank() && !dados.novoEmail().equalsIgnoreCase(emailAtual)) {
            // Verifica se o novo email já está em uso por OUTRO usuário
            Optional<Usuario> usuarioComNovoEmail = usuarioRepository.findByEmail(dados.novoEmail());
            if (usuarioComNovoEmail.isPresent() && !usuarioComNovoEmail.get().getId().equals(adminParaAtualizar.getId())) {
                throw new DataIntegrityViolationException("O novo email fornecido já está em uso por outra conta.");
            }
            // Atualiza o email
            adminParaAtualizar.setEmail(dados.novoEmail());
            modificado = true;
        }

        // 4. Atualiza a Senha (se fornecida)
        if (dados.novaSenha() != null && !dados.novaSenha().isBlank()) {
            // Criptografa a nova senha antes de salvar
            adminParaAtualizar.setSenha(passwordEncoder.encode(dados.novaSenha()));
            modificado = true;
        }

        // 5. Salva as alterações apenas se algo foi modificado
        if (modificado) {
            return usuarioRepository.save(adminParaAtualizar);
        } else {
            // Se nada foi alterado (ex: enviou só a senha atual), retorna o usuário sem salvar
            return adminParaAtualizar;
        }
    }
}
