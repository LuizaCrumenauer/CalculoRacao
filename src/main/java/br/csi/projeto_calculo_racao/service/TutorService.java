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

    @Transactional
    public Tutor atualizarPerfilCompleto( DadosAtualizacaoPerfilTutorDTO dados) { // Verifique o nome do seu DTO
        // 1. Identifica o tutor logado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailAtual = authentication.getName();
        Tutor tutorParaAtualizar = this.repository.findByUsuario_Email(emailAtual)
                .orElseThrow(() -> new NoSuchElementException("Perfil de tutor não encontrado para o usuário logado."));
        Usuario usuarioAssociado = tutorParaAtualizar.getUsuario();

        // 2. Atualiza os campos do Tutor (nome, telefone, endereço)
        if (dados.nome() != null && !dados.nome().isBlank()) {
            tutorParaAtualizar.setNome(dados.nome());
        }
        if (dados.telefone() != null) { // Permite telefone em branco? Ajuste se necessário
            tutorParaAtualizar.setTelefone(dados.telefone());
        }
        if (dados.endereco() != null) {
            // Validação do endereço já deve ocorrer via @Valid no DTO
            tutorParaAtualizar.setEndereco(dados.endereco());
        }

        // Atualiza CPF (se fornecido e diferente)
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

        // 3. Atualiza o Email (se fornecido, diferente E senha correta)
        boolean emailAlterado = false; // Flag para saber se precisamos salvar o usuário explicitamente (embora JPA possa fazer automaticamente)

        // Cenário 1: Novo email foi fornecido, está preenchido E é diferente do email atual.
        if (dados.novoEmail() != null && !dados.novoEmail().isBlank() && !dados.novoEmail().equalsIgnoreCase(emailAtual)) {
            // Neste caso, a senha atual é OBRIGATÓRIA
            if (dados.senhaAtual() == null || dados.senhaAtual().isBlank()) {
                throw new BadCredentialsException("A senha atual é obrigatória para alterar o email.");
            }
            // Verifica se a senha atual está correta
            if (!passwordEncoder.matches(dados.senhaAtual(), usuarioAssociado.getSenha())) {
                throw new BadCredentialsException("Senha atual incorreta.");
            }
            // Verifica se o novo email já está em uso por OUTRO usuário
            Optional<Usuario> usuarioComNovoEmail = usuarioRepository.findByEmail(dados.novoEmail());
            if (usuarioComNovoEmail.isPresent() && !usuarioComNovoEmail.get().getId().equals(usuarioAssociado.getId())) {
                throw new DataIntegrityViolationException("O novo email fornecido já está em uso por outra conta.");
            }
            // Se tudo estiver OK, atualiza o email
            usuarioAssociado.setEmail(dados.novoEmail());
            emailAlterado = true; // Marca que o email foi alterado
        }
        // Cenário 2: Novo email foi fornecido, mas é IGUAL ao email atual.
        else if (dados.novoEmail() != null && !dados.novoEmail().isBlank() && dados.novoEmail().equalsIgnoreCase(emailAtual)) {
            // Não faz nada com o email. A senha atual, se fornecida, é ignorada.
            System.out.println("Novo email fornecido é igual ao atual. Nenhuma alteração de email realizada.");
        }
        // Cenário 3: Novo email NÃO foi fornecido (null ou em branco).
        else if (dados.novoEmail() == null || dados.novoEmail().isBlank()) {
            // Não faz nada com o email. A senha atual, se fornecida, é ignorada.
            System.out.println("Nenhum novo email fornecido. Nenhuma alteração de email realizada.");
        }

        // 4. Salva as entidades
        // Salvar o Tutor. O JPA/Hibernate gerenciará o salvamento do Usuario associado
        // se ele foi modificado dentro da mesma transação (@Transactional).
        Tutor tutorSalvo = this.repository.save(tutorParaAtualizar);

        return tutorSalvo; // Retorna o tutor com os dados atualizados
    }
}
