package br.csi.projeto_calculo_racao.service;

import br.csi.projeto_calculo_racao.DTO.AdminCreateDTO;
import br.csi.projeto_calculo_racao.model.usuario.Role;
import br.csi.projeto_calculo_racao.model.usuario.Usuario;
import br.csi.projeto_calculo_racao.model.usuario.UsuarioRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
}
