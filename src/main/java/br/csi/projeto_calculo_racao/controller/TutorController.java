package br.csi.projeto_calculo_racao.controller;

import br.csi.projeto_calculo_racao.DTO.DadosCadastroTutorDTO;
import br.csi.projeto_calculo_racao.model.tutor.Tutor;
import br.csi.projeto_calculo_racao.service.TutorService;
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
public class TutorController {

    private final TutorService service;

    public TutorController(TutorService service) {
        this.service = service;
    }

    @PostMapping("/cadastrar")
    @Transactional
    public ResponseEntity<Tutor> cadastrar(@RequestBody @Valid DadosCadastroTutorDTO dados, UriComponentsBuilder uriBuilder) {
        Tutor tutor = this.service.cadastrarTutor(dados);
        URI uri = uriBuilder.path("/tutores/{uuid}").buildAndExpand(tutor.getUuid()).toUri();
        return ResponseEntity.created(uri).body(tutor);
    }

    @GetMapping("/perfil")
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
    public ResponseEntity<List<Tutor>> listar() {
        return ResponseEntity.ok(this.service.listarTutores());
    }

    @GetMapping("/buscar/{uuid}")
    public ResponseEntity<Tutor> buscar(@PathVariable UUID uuid) {
        Tutor tutor = this.service.buscarTutor(uuid);
        return ResponseEntity.ok(tutor);
    }

    @PutMapping("/atualizar")
    public ResponseEntity<Tutor> atualizar(@RequestBody Tutor tutor) {
        this.service.atualizar(tutor);
        return ResponseEntity.ok(tutor);
    }

    @DeleteMapping("/excluir/{uuid}")
    @Transactional
    public ResponseEntity<Void> excluir(@PathVariable UUID uuid) {
        this.service.excluir(uuid);
        return ResponseEntity.noContent().build();
    }
}
