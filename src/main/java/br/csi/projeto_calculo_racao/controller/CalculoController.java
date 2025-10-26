package br.csi.projeto_calculo_racao.controller;

import br.csi.projeto_calculo_racao.DTO.DadosCalculoDTO;
import br.csi.projeto_calculo_racao.model.calculo.Calculo;
import br.csi.projeto_calculo_racao.service.CalculoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/calculos")
@Tag(name = "Cálculos de Ração", description = "Endpoint para calcular a quantidade de ração dos pets")
public class CalculoController {

    private final CalculoService calculoService;

    public CalculoController(CalculoService calculoService) {
        this.calculoService = calculoService;
    }

    @PostMapping("/calcular/{petUuid}")
    @Operation(summary = "Realizar novo cálculo",
            description = "Calcula a necessidade diária de ração para um pet específico com base no seu peso, nível de atividade e na ração (via ID ou EM manual).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cálculo realizado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Calculo.class))),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos (ex: peso negativo, EM manual negativo, nível de atividade em branco)",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autenticado",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Pet não encontrado com o UUID fornecido",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor (ex: ração não encontrada, falha na lógica de cálculo)",
                    content = @Content)
    })
    public ResponseEntity<Calculo> calcular(
            @Parameter(description = "UUID do pet para o qual o cálculo será feito", required = true)
            @PathVariable UUID petUuid,
            @RequestBody @Valid DadosCalculoDTO dados) {
        Calculo resultado = this.calculoService.realizarCalculo (petUuid, dados);
        return ResponseEntity.ok(resultado);
    }
}
