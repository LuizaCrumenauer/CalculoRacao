package br.csi.projeto_calculo_racao.controller;

import br.csi.projeto_calculo_racao.model.pet.Pet;
import br.csi.projeto_calculo_racao.service.PetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/pets")
public class PetController {
    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @PostMapping("/cadastrar/{idTutor}")
    public ResponseEntity<Pet> cadastrar(@RequestBody Pet pet, @PathVariable Long idTutor, UriComponentsBuilder uriBuilder) {
        this.petService.cadastar ( pet, idTutor );
        URI uri = uriBuilder.path("/pets/{id}").buildAndExpand(pet.getId()).toUri();
        return ResponseEntity.created(uri).body(pet);
    }


    @PutMapping("/atualizar/{idTutor}")
    public ResponseEntity<Pet> atualizar(@RequestBody Pet pet, @PathVariable Long idTutor) {
        this.petService.atualizar(pet, idTutor);
        return ResponseEntity.ok(pet);
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Pet>> listar() {
        return ResponseEntity.ok(this.petService.listarPets ());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pet> buscarUm(@PathVariable Long id) {
        return ResponseEntity.ok(this.petService.buscarPetPorId ( id ));
    }

    @DeleteMapping("/excluir/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        this.petService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
