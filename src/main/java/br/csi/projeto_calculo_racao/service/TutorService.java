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
    public Tutor atualizarPerfilCompleto(DadosAtualizacaoPerfilTutorDTO dados) {
        // 1. IDENTIFICA O TUTOR E USUÁRIO LOGADO
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailAtual = authentication.getName();
        Tutor tutorParaAtualizar = this.repository.findByUsuario_Email(emailAtual)
                .orElseThrow(() -> new NoSuchElementException("Perfil de tutor não encontrado para o usuário logado."));
        Usuario usuarioAssociado = tutorParaAtualizar.getUsuario();

        // 2. ATUALIZA DADOS SIMPLES (NOME, TELEFONE, ENDEREÇO)
        // Esses campos não exigem confirmação de senha.
        if (dados.nome() != null && !dados.nome().isBlank()) {
            tutorParaAtualizar.setNome(dados.nome());
        }
        if (dados.telefone() != null) {
            // A validação do @Pattern no DTO já tratou o formato
            tutorParaAtualizar.setTelefone(dados.telefone());
        }
        if (dados.endereco() != null) {
            tutorParaAtualizar.setEndereco(dados.endereco());
        }

        // 3. ATUALIZA DADOS DO TUTOR (CPF)
        // Também não exige senha, mas exige verificação de duplicidade.
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

        // 4. VERIFICA OPERAÇÕES CRÍTICAS (EMAIL E SENHA)
        // Define o que o usuário está TENTANDO fazer.
        boolean querMudarEmail = dados.novoEmail() != null && !dados.novoEmail().isBlank() && !dados.novoEmail().equalsIgnoreCase(emailAtual);
        boolean querMudarSenha = dados.novaSenha() != null && !dados.novaSenha().isBlank();

        // 5. VALIDA A SENHA ATUAL (SE NECESSÁRIO)
        // Se o usuário quer mudar email OU senha, ele PRECISA fornecer a senha atual correta.
        if (querMudarEmail || querMudarSenha) {
            if (dados.senhaAtual() == null || dados.senhaAtual().isBlank()) {
                throw new BadCredentialsException("A senha atual é obrigatória para alterar o email ou a senha.");
            }

            // Verifica se a senha atual fornecida está correta
            if (!passwordEncoder.matches(dados.senhaAtual(), usuarioAssociado.getSenha())) {
                throw new BadCredentialsException("Senha atual incorreta.");
            }

            // Se chegamos aqui, o usuário está autenticado para realizar operações críticas.
        }

        // 6. EXECUTA A MUDANÇA DE EMAIL (SE SOLICITADO E VALIDADO)
        if (querMudarEmail) {
            // A senha já foi validada no passo 5.
            // verifica se o NOVO email está disponível.
            Optional<Usuario> usuarioComNovoEmail = usuarioRepository.findByEmail(dados.novoEmail());

            if (usuarioComNovoEmail.isPresent() && !usuarioComNovoEmail.get().getId().equals(usuarioAssociado.getId())) {
                throw new DataIntegrityViolationException("O novo email fornecido já está em uso por outra conta.");
            }

            // Se tudo estiver OK, atualiza o email
            usuarioAssociado.setEmail(dados.novoEmail());
            System.out.println("Email do usuário atualizado para: " + dados.novoEmail());
        }

        // 7. EXECUTA A MUDANÇA DE SENHA (SE SOLICITADO E VALIDADO)
        if (querMudarSenha) {
            // A senha já foi validada no passo 5.

            // (Opcional) Você pode adicionar mais validações aqui, como "nova senha não pode ser igual à antiga"
            if (passwordEncoder.matches(dados.novaSenha(), usuarioAssociado.getSenha())) {
                // No seu caso, o TratadorDeErros já pega BadCredentials
                throw new BadCredentialsException("A nova senha não pode ser igual à senha atual.");
            }

            // Se tudo estiver OK, codifica e salva a nova senha
            usuarioAssociado.setSenha(passwordEncoder.encode(dados.novaSenha()));
            System.out.println("Senha do usuário atualizada.");
        }

        // 8. SALVA AS MUDANÇAS
        // O @Transactional garante que tanto o 'tutorParaAtualizar' quanto o 'usuarioAssociado'
        // serão salvos no banco de dados.
        Tutor tutorSalvo = this.repository.save(tutorParaAtualizar);

        return tutorSalvo; // Retorna o tutor com os dados atualizados
    }
}
