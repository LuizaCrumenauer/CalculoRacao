package br.csi.projeto_calculo_racao.controller;

import br.csi.projeto_calculo_racao.DTO.AdminCreateDTO;
import br.csi.projeto_calculo_racao.DTO.DadosAtualizacaoPerfilAdminDTO;
import br.csi.projeto_calculo_racao.model.usuario.Usuario;
import br.csi.projeto_calculo_racao.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuários e Administração", description = "Operações relacionadas a contas de usuário e permissões")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PutMapping("/tornar-admin/{usuarioId}")
    @Operation(summary = "Promover usuário a Admin (Admin)",
            description = "Permite que um ADMIN logado promova outro usuário (existente) para a role de ADMIN, usando o ID numérico do usuário.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário promovido a ADMIN com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Usuario.class))),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado (usuário logado não é ADMIN)", content = @Content),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado com o ID fornecido", content = @Content)
    })
    public ResponseEntity<Usuario> tornarAdmin(
            @Parameter(description = "ID (numérico) do usuário a ser promovido", required = true)
            @PathVariable Long usuarioId) {
        Usuario usuarioAtualizado = usuarioService.tornarAdmin(usuarioId);
        return ResponseEntity.ok(usuarioAtualizado);
    }

    @PostMapping("/admin")
    @Operation(summary = "Criar novo usuário Admin (Admin)",
            description = "Cria um novo usuário já com a role de ADMIN. Requer que o usuário logado também seja ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário Admin criado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Usuario.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos (ex: email mal formatado, senha em branco)", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado (usuário logado não é ADMIN)", content = @Content),
            @ApiResponse(responseCode = "409", description = "Email já cadastrado", content = @Content)
    })
    public ResponseEntity<Usuario> createAdmin(@RequestBody @Valid AdminCreateDTO dados) {
        Usuario novoAdmin = usuarioService.createAdmin(dados);
        return ResponseEntity.status( HttpStatus.CREATED).body(novoAdmin);
    }

    @PutMapping("/admin/atualizar")
    @Operation(summary = "Atualizar email/senha do admin logado", description = "Permite ao administrador autenticado alterar seu próprio email e/ou senha, exigindo a senha atual para confirmação.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Credenciais atualizadas com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Usuario.class))), // Retorna o usuário atualizado
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos (ex: email mal formatado, senha nova curta)", content = @Content),
            @ApiResponse(responseCode = "401", description = "Senha atual incorreta", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado (usuário logado não é admin)", content = @Content),
            @ApiResponse(responseCode = "409", description = "O novo email já está em uso por outra conta", content = @Content)
    })
    public ResponseEntity<Usuario> alterarAdmin(@RequestBody @Valid DadosAtualizacaoPerfilAdminDTO dados) {
        Usuario usuarioAtualizado = usuarioService.atualizarAdmin (dados);
        return ResponseEntity.ok(usuarioAtualizado);
    }
}
