package br.csi.projeto_calculo_racao.controller;

import br.csi.projeto_calculo_racao.DTO.AdminCreateDTO;
import br.csi.projeto_calculo_racao.model.usuario.Usuario;
import br.csi.projeto_calculo_racao.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
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
}
