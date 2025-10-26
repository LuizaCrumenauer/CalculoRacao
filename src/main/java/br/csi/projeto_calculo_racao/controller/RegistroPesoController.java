package br.csi.projeto_calculo_racao.controller;

import br.csi.projeto_calculo_racao.DTO.DadosAtualizacaoPesoDTO; // <-- Importe
import br.csi.projeto_calculo_racao.DTO.DadosRegistroPesoDTO;
import br.csi.projeto_calculo_racao.model.registroPeso.RegistroPeso;
import br.csi.projeto_calculo_racao.service.RegistroPesoService;
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
    public ResponseEntity<Void> registrar(@PathVariable UUID petUuid, @RequestBody @Valid DadosRegistroPesoDTO dados) {
        service.registrarNovoPeso(petUuid, dados);
        return ResponseEntity.status(201).build();
    }

    @GetMapping("/historico/{petUuid}")
    public ResponseEntity<List<RegistroPeso>> historico(@PathVariable UUID petUuid) {
        List<RegistroPeso> historico = service.historicoDePesoPorPet(petUuid);
        return ResponseEntity.ok(historico);
    }

    // --- NOVO ENDPOINT DE ATUALIZAÇÃO ---
    @PutMapping("/atualizar/{idRegistro}")
    @Transactional
    public ResponseEntity<RegistroPeso> atualizar(@PathVariable Long idRegistro, @RequestBody @Valid DadosAtualizacaoPesoDTO dados) {
        RegistroPeso registroAtualizado = service.atualizar(idRegistro, dados);
        return ResponseEntity.ok(registroAtualizado);
    }

    // --- NOVO ENDPOINT DE EXCLUSÃO ---
    @DeleteMapping("/excluir/{idRegistro}")
    @Transactional
    public ResponseEntity<Void> excluir(@PathVariable Long idRegistro) {
        service.excluir(idRegistro);
        return ResponseEntity.noContent().build(); // Retorna 204 No Content
    }
}
