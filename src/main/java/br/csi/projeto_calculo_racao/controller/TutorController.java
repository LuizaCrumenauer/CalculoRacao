package br.csi.projeto_calculo_racao.controller;

import br.csi.projeto_calculo_racao.model.tutor.Tutor;
import br.csi.projeto_calculo_racao.service.TutorService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Tutor> cadastrar(@RequestBody @Valid Tutor tutor, UriComponentsBuilder uriBuilder) {
        this.service.salvar(tutor);
        URI uri = uriBuilder.path("/tutores/{uuid}").buildAndExpand(tutor.getUuid ()).toUri();
        return ResponseEntity.created(uri).body(tutor);
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Tutor>> listar() {
        return ResponseEntity.ok(this.service.listarTutores ());
    }

    @GetMapping("/buscar/{uuid}")
    public ResponseEntity<Tutor> buscar( @PathVariable UUID uuid ) {
        Tutor tutor = this.service.buscarTutor ( uuid );
        return ResponseEntity.ok (tutor);
    }

    @PutMapping("/atualizar")
    public ResponseEntity<Tutor> atualizar( @RequestBody Tutor tutor) {
        this.service.atualizar (tutor);
        return ResponseEntity.ok (tutor);
    }

    @DeleteMapping("/excluir/{uuid}")
    @Transactional
    public ResponseEntity<Void> excluir(@PathVariable UUID uuid) {
        this.service.excluir (uuid);
        return ResponseEntity.noContent().build();
    }
}
