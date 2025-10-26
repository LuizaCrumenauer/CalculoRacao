package br.csi.projeto_calculo_racao.controller;

import br.csi.projeto_calculo_racao.model.pet.Pet;
import br.csi.projeto_calculo_racao.service.PetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Pets", description = "Gerenciamento dos animais de estimação")
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @PostMapping( "/cadastrar/{uuidTutor}")
    @Transactional
    @Operation(summary = "Cadastrar novo pet",
            description = "Cadastra um novo pet e o associa a um tutor existente. Requer autenticação de tutor.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pet cadastrado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Pet.class))),
            @ApiResponse(responseCode = "400", description = "Dados do pet inválidos (ex: nome em branco, data de nascimento futura)", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
            @ApiResponse(responseCode = "404", description = "Tutor não encontrado com o UUID fornecido", content = @Content)
    })
    public ResponseEntity<Pet> cadastrar(
            @Parameter(description = "UUID do tutor que será o dono do pet", required = true)
            @RequestBody @Valid Pet pet, @PathVariable UUID uuidTutor, UriComponentsBuilder uriBuilder) {
        this.petService.cadastar ( pet, uuidTutor );
        URI uri = uriBuilder.path("/pets/{uuid}").buildAndExpand(pet.getUuid ()).toUri();
        return ResponseEntity.created(uri).body(pet);
    }


    @PutMapping("/atualizar/{uuidTutor}")
    @Transactional
    @Operation(summary = "Atualizar pet existente",
            description = "Atualiza os dados de um pet. O corpo deve conter o Pet com seu UUID/ID e os campos alterados. O UUID do tutor no path é usado para verificação de propriedade.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pet atualizado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Pet.class))),
            @ApiResponse(responseCode = "400", description = "Dados do pet inválidos (ex: nome em branco)", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
            @ApiResponse(responseCode = "404", description = "Pet (no corpo) ou Tutor (no path) não encontrado", content = @Content)
    })
    public ResponseEntity<Pet> atualizar(
            @Parameter(description = "UUID do tutor dono do pet (para verificação)", required = true)
            @RequestBody Pet pet, @PathVariable UUID uuidTutor) {
        this.petService.atualizar(pet, uuidTutor);
        return ResponseEntity.ok(pet);
    }

    @GetMapping("/listar")
    @Operation(summary = "Listar todos os pets (Somente Admin)",
            description = "Retorna uma lista de todos os pets de todos os tutores. Requer permissão de ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de pets retornada",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Pet.class)))),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado (não é admin)", content = @Content)
    })
    public ResponseEntity<List<Pet>> listar() {
        return ResponseEntity.ok(this.petService.listarPets ());
    }

    @GetMapping("/buscar/{uuid}")
    @Operation(summary = "Buscar pet por UUID",
            description = "Retorna os dados de um pet específico. Requer autenticação (dono ou admin).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pet encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Pet.class))),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
            @ApiResponse(responseCode = "404", description = "Pet não encontrado com o UUID fornecido", content = @Content)
    })
    public ResponseEntity<Pet> buscar(
            @Parameter(description = "UUID do pet a ser buscado", required = true)
            @PathVariable UUID uuid) {
        return ResponseEntity.ok(this.petService.buscarPet ( uuid ));
    }

    @DeleteMapping("/excluir/{uuid}")
    @Transactional
    @Operation(summary = "Excluir pet",
            description = "Exclui um pet do sistema. Requer autenticação (dono ou admin).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Pet excluído com sucesso", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
            @ApiResponse(responseCode = "404", description = "Pet não encontrado com o UUID fornecido", content = @Content)
    })
    public ResponseEntity<Void> excluir(
            @Parameter(description = "UUID do pet a ser excluído", required = true)
            @PathVariable UUID uuid) {
        this.petService.excluir(uuid);
        return ResponseEntity.noContent().build();
    }

}
