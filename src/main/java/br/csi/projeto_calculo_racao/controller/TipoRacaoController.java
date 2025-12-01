package br.csi.projeto_calculo_racao.controller;

import br.csi.projeto_calculo_racao.model.tipoRacao.TipoRacao;
import br.csi.projeto_calculo_racao.model.tipoRacao.TipoRacaoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/racoes")
@Tag(name = "Tipos de Ração", description = "Listagem de rações cadastradas")
public class TipoRacaoController {

    private final TipoRacaoRepository repository;

    public TipoRacaoController(TipoRacaoRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/listar")
    @Operation(summary = "Listar todos os tipos de ração", description = "Retorna a lista de rações disponíveis para seleção.")
    public ResponseEntity<List<TipoRacao>> listar() {
        return ResponseEntity.ok(repository.findAll());
    }
}
