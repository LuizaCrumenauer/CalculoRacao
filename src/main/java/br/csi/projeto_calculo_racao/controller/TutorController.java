package br.csi.projeto_calculo_racao.controller;

import br.csi.projeto_calculo_racao.DTO.DadosAtualizacaoPerfilTutorDTO;
import br.csi.projeto_calculo_racao.DTO.DadosCadastroTutorDTO;
import br.csi.projeto_calculo_racao.model.tutor.Tutor;
import br.csi.projeto_calculo_racao.service.TutorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;


import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tutores")
@Tag(name = "Tutores", description = "Operações relacionadas aos tutores e seus perfis")
public class TutorController {

    private final TutorService service;

    public TutorController(TutorService service) {
        this.service = service;
    }

    @PostMapping("/cadastrar")
    @Transactional
    @Operation(summary = "Cadastrar um novo tutor", description = "Cria um novo usuário (com role USER) e um perfil de tutor associado. Acesso público.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tutor cadastrado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Tutor.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos (ex: email, cpf, endereço)", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflito de dados (CPF ou Email já cadastrado)", content = @Content)
    })
    public ResponseEntity<Tutor> cadastrar(@RequestBody @Valid DadosCadastroTutorDTO dados, UriComponentsBuilder uriBuilder) {
        Tutor tutor = this.service.cadastrarTutor(dados);
        URI uri = uriBuilder.path("/tutores/{uuid}").buildAndExpand(tutor.getUuid()).toUri();
        return ResponseEntity.created(uri).body(tutor);
    }

    @GetMapping("/perfil")
    @Operation(summary = "Buscar perfil do tutor logado", description = "Retorna os dados do perfil de tutor associado ao usuário autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil do tutor encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Tutor.class))),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
            @ApiResponse(responseCode = "404", description = "Perfil de tutor não encontrado para o usuário logado (pode acontecer se um admin sem tutor tentar acessar)", content = @Content)
    })
    public ResponseEntity<Tutor> buscarPerfil() {
        // Pega o objeto de autenticação do contexto de segurança do Spring
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Extrai os detalhes do usuário (que implementamos no AutenticacaoService)
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        // O "username" no nosso caso é o email
        String email = userDetails.getUsername();

        // Usa o novo mtodo do serviço para buscar o tutor pelo email
        Tutor tutor = this.service.buscarTutorPorEmail(email);
        return ResponseEntity.ok(tutor);
    }

    @GetMapping("/listar")
    @Operation(summary = "Listar todos os tutores (Apenas Admin)", description = "Retorna uma lista de todos os perfis de tutores cadastrados. Requer permissão de ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de tutores retornada",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Tutor.class)))),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado (não é admin)", content = @Content)
    })
    public ResponseEntity<List<Tutor>> listar() {
        return ResponseEntity.ok(this.service.listarTutores());
    }

    @GetMapping("/buscar/{uuid}")
    @Operation(summary = "Buscar tutor por UUID", description = "Retorna os dados de um tutor específico. Requer autenticação.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tutor encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Tutor.class))),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
            @ApiResponse(responseCode = "404", description = "Tutor não encontrado", content = @Content)
    })
    public ResponseEntity<Tutor> buscar(
            @Parameter(description = "UUID do tutor a ser buscado", required = true)
            @PathVariable UUID uuid) {
        Tutor tutor = this.service.buscarTutor(uuid);
        return ResponseEntity.ok(tutor);
    }

    @PutMapping("/atualizar")
    @Transactional
    @Operation(summary = "Atualizar perfil completo do tutor logado", description = "Atualiza nome, telefone, endereço, CPF (opcional) e email (opcional, requer senha atual).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil atualizado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Tutor.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos (ex: formato de CPF/Email)", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autenticado ou Senha atual incorreta (ao tentar mudar email)", content = @Content),
            @ApiResponse(responseCode = "404", description = "Perfil de tutor não encontrado", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflito: Novo email ou CPF já em uso", content = @Content)
    })
    public ResponseEntity<Tutor> atualizarPerfilCompleto(
            @Parameter(description = "Dados a serem atualizados. Campos não fornecidos (null) não serão alterados. Para mudar email, 'senhaAtual' é obrigatória.") // Documentação do parâmetro
            @RequestBody @Valid DadosAtualizacaoPerfilTutorDTO dados) { // Usa o novo DTO
        Tutor tutorAtualizado = service.atualizarPerfilCompleto(dados);
        return ResponseEntity.ok(tutorAtualizado);
    }

    @DeleteMapping("/excluir/{uuid}")
    @Transactional
    @Operation(summary = "Excluir tutor", description = "Exclui o perfil de tutor e o usuário associado. Requer autenticação.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tutor excluído com sucesso", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
            @ApiResponse(responseCode = "404", description = "Tutor não encontrado", content = @Content)
            // Considerar adicionar 403 se apenas admins puderem excluir (não implementado)
    })
    public ResponseEntity<Void> excluir(
            @Parameter(description = "UUID do tutor a ser excluído", required = true)
            @PathVariable UUID uuid) {
        this.service.excluir(uuid);
        return ResponseEntity.noContent().build();
    }
}
