package br.csi.projeto_calculo_racao.controller;

import br.csi.projeto_calculo_racao.DTO.AdminCreateDTO;
import br.csi.projeto_calculo_racao.DTO.DadosAtualizacaoPerfilAdminDTO;
import br.csi.projeto_calculo_racao.model.usuario.Usuario;
import br.csi.projeto_calculo_racao.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
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

    // Endpoint para um ADMIN tornar outro usuário ADMIN
    @PutMapping("/tornar-admin/{usuarioId}")
    public ResponseEntity<Usuario> tornarAdmin(@PathVariable Long usuarioId) {
        Usuario usuarioAtualizado = usuarioService.tornarAdmin(usuarioId);
        return ResponseEntity.ok(usuarioAtualizado);
    }

    @PostMapping("/admin")
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
