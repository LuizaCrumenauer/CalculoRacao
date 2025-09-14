package br.csi.projeto_calculo_racao.controller;

import br.csi.projeto_calculo_racao.model.pet.Pet;
import br.csi.projeto_calculo_racao.service.PetService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/pets")
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @PostMapping("/cadastrar/{uuidTutor}")
    @Transactional
    public ResponseEntity<Pet> cadastrar( @RequestBody @Valid Pet pet, @PathVariable UUID uuidTutor, UriComponentsBuilder uriBuilder) {
        this.petService.cadastar ( pet, uuidTutor );
        URI uri = uriBuilder.path("/pets/{uuid}").buildAndExpand(pet.getUuid ()).toUri();
        return ResponseEntity.created(uri).body(pet);
    }


    @PutMapping("/atualizar/{uuidTutor}")
    @Transactional
    public ResponseEntity<Pet> atualizar(@RequestBody Pet pet, @PathVariable UUID uuidTutor) {
        this.petService.atualizar(pet, uuidTutor);
        return ResponseEntity.ok(pet);
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Pet>> listar() {
        return ResponseEntity.ok(this.petService.listarPets ());
    }

    @GetMapping("/buscar/{uuid}")
    public ResponseEntity<Pet> buscar(@PathVariable UUID uuid) {
        return ResponseEntity.ok(this.petService.buscarPet ( uuid ));
    }

    @DeleteMapping("/excluir/{uuid}")
    public ResponseEntity<Void> excluir(@PathVariable UUID uuid) {
        this.petService.excluir(uuid);
        return ResponseEntity.noContent().build();
    }
}
