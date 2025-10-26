package br.csi.projeto_calculo_racao.controller;

import br.csi.projeto_calculo_racao.DTO.DadosAtualizacaoPesoDTO; // <-- Importe
import br.csi.projeto_calculo_racao.DTO.DadosRegistroPesoDTO;
import br.csi.projeto_calculo_racao.model.registroPeso.RegistroPeso;
import br.csi.projeto_calculo_racao.service.RegistroPesoService;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/pesos")
@Tag(name = "Registro de Peso", description = "Gerenciamento do histórico de peso dos pets")
public class RegistroPesoController {

    private final RegistroPesoService service;

    public RegistroPesoController(RegistroPesoService service) {
        this.service = service;
    }

    @PostMapping("/registrar/{petUuid}")
    @Transactional
    @Operation(summary = "Registrar novo peso para um pet",
            description = "Registra um novo peso para o pet (com a data atual). Requer autenticação do dono do pet.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Peso registrado com sucesso",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Dados inválidos (ex: peso negativo ou nulo)",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
            @ApiResponse(responseCode = "403", description = "Usuário não é o dono do pet", content = @Content),
            @ApiResponse(responseCode = "404", description = "Pet não encontrado com o UUID fornecido", content = @Content)
    })
    public ResponseEntity<Void> registrar(
            @Parameter(description = "UUID do pet que terá o peso registrado", required = true)
            @PathVariable UUID petUuid,
            @RequestBody @Valid DadosRegistroPesoDTO dados) {
        service.registrarNovoPeso(petUuid, dados);
        return ResponseEntity.status(201).build();
    }

    @GetMapping("/historico/{petUuid}")
    @Operation(summary = "Listar histórico de peso do pet",
            description = "Retorna uma lista de todos os registros de peso de um pet específico, ordenados por data.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Histórico de peso retornado com sucesso",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = RegistroPeso.class)))),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
            @ApiResponse(responseCode = "403", description = "Usuário não é o dono do pet", content = @Content),
            @ApiResponse(responseCode = "404", description = "Pet não encontrado", content = @Content)
    })
    public ResponseEntity<List<RegistroPeso>> historico(
            @Parameter(description = "UUID do pet para buscar o histórico", required = true)
            @PathVariable UUID petUuid) {
        List<RegistroPeso> historico = service.historicoDePesoPorPet(petUuid);
        return ResponseEntity.ok(historico);
    }

    @PutMapping("/atualizar/{idRegistro}")
    @Transactional
    @Operation(summary = "Atualizar um registro de peso",
            description = "Atualiza o peso e a data de um registro de peso específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro atualizado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RegistroPeso.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos (ex: peso negativo, data futura)",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
            @ApiResponse(responseCode = "403", description = "Usuário não é o dono deste registro", content = @Content),
            @ApiResponse(responseCode = "404", description = "Registro de peso não encontrado", content = @Content)
    })
    public ResponseEntity<RegistroPeso> atualizar(
            @Parameter(description = "ID (numérico) do registro de peso a ser atualizado", required = true)
            @PathVariable Long idRegistro,
            @RequestBody @Valid DadosAtualizacaoPesoDTO dados) {
        RegistroPeso registroAtualizado = service.atualizar(idRegistro, dados);
        return ResponseEntity.ok(registroAtualizado);
    }

    @DeleteMapping("/excluir/{idRegistro}")
    @Transactional
    @Operation(summary = "Excluir um registro de peso",
            description = "Exclui permanentemente um registro de peso.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Registro excluído com sucesso",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
            @ApiResponse(responseCode = "403", description = "Usuário não é o dono deste registro", content = @Content),
            @ApiResponse(responseCode = "404", description = "Registro de peso não encontrado", content = @Content)
    })
    public ResponseEntity<Void> excluir(
            @Parameter(description = "ID (numérico) do registro de peso a ser excluído", required = true)
            @PathVariable Long idRegistro) {
        service.excluir(idRegistro);
        return ResponseEntity.noContent().build();
    }
}
