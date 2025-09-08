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
        URI uri = uriBuilder.path("/tutores/{id}").buildAndExpand(tutor.getId()).toUri();
        return ResponseEntity.created(uri).body(tutor);
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Tutor>> listar() {
        return ResponseEntity.ok(this.service.listarTutores ());
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<Tutor> buscar(@PathVariable Long id) {
        Tutor tutor = this.service.getTutor (id);
        return ResponseEntity.ok (tutor);
    }

    @PutMapping("/atualizar")
    public ResponseEntity<Tutor> atualizar( @RequestBody Tutor tutor) {
        this.service.atualizar (tutor);
        return ResponseEntity.ok (tutor);
    }

    @DeleteMapping("/excluir/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        this.service.excluir (id);
        return ResponseEntity.noContent().build();
    }
}
