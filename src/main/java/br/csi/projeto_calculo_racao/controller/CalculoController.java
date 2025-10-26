package br.csi.projeto_calculo_racao.controller;

import br.csi.projeto_calculo_racao.DTO.DadosCalculoDTO;
import br.csi.projeto_calculo_racao.model.calculo.Calculo;
import br.csi.projeto_calculo_racao.service.CalculoService;
import io.swagger.v3.oas.annotations.tags.Tag;
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
    public ResponseEntity<Calculo> calcular( @PathVariable UUID petUuid, @RequestBody DadosCalculoDTO dados) {
        Calculo resultado = this.calculoService.realizarCalculo (petUuid, dados);
        return ResponseEntity.ok(resultado);
    }
}
