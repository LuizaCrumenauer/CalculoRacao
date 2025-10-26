package br.csi.projeto_calculo_racao.controller;

import br.csi.projeto_calculo_racao.DTO.ItemSaudeDTO;
import br.csi.projeto_calculo_racao.DTO.RegistroSaudeDTO;
import br.csi.projeto_calculo_racao.model.registroSaude.ItemSaude;
import br.csi.projeto_calculo_racao.model.registroSaude.RegistroSaude;
import br.csi.projeto_calculo_racao.model.tutor.Tutor;
import br.csi.projeto_calculo_racao.model.usuario.Usuario;
import br.csi.projeto_calculo_racao.service.SaudeService;
import br.csi.projeto_calculo_racao.service.TutorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/saude")
@Tag(name = "Saúde do Pet", description = "Gerenciamento de itens e registros de saúde")
public class SaudeController {

    private final SaudeService saudeService;
    private final TutorService tutorService;

    public SaudeController(SaudeService saudeService, TutorService tutorService) {
        this.saudeService = saudeService;
        this.tutorService = tutorService;
    }

    @GetMapping("/itens/listar")
    @Operation(summary = "Listar itens de saúde disponíveis",
            description = "Lista os itens de saúde. Se o usuário for ADMIN, retorna TODOS os itens. Se for TUTOR, retorna os itens globais (sem tutor) e os itens personalizados do próprio tutor.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de itens retornada",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ItemSaude.class)))),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content)
    })
    public ResponseEntity<List<ItemSaude>> getItensDisponiveis() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .map( GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));

        List<ItemSaude> itens;

        if (isAdmin) {
            // Se for ADMIN, chama o busca TUDO.
            itens = saudeService.getAllItens();
        } else {
            // Se for um usuário comum (TUTOR), busca os seus e os do admin
            Tutor tutorLogado = getTutorLogado();
            itens = saudeService.getItensDisponiveis(tutorLogado);
        }

        return ResponseEntity.ok(itens);
    }

    @PostMapping("/itens")
    @Operation(summary = "Criar item de saúde personalizado (Tutor)",
            description = "Permite a um TUTOR autenticado criar um novo item de saúde (vacina, remédio) que ficará visível apenas para ele.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Item personalizado criado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ItemSaude.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos (ex: nome ou tipo em branco)", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content)
    })
    public ResponseEntity<ItemSaude> criarItemPersonalizado(@RequestBody @Valid ItemSaudeDTO dto) {
        Tutor tutorLogado = getTutorLogado();
        ItemSaude novoItem = saudeService.criarItem(dto, tutorLogado);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoItem);
    }

    @PostMapping("/itens/admin")
    @Operation(summary = "Criar item de saúde global (Admin)",
            description = "Permite a um ADMIN criar um novo item de saúde que ficará disponível para TODOS os usuários.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Item global criado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ItemSaude.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos (ex: nome ou tipo em branco)", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado (usuário não é ADMIN)", content = @Content)
    })
    public ResponseEntity<ItemSaude> criarItemGlobal(@RequestBody @Valid ItemSaudeDTO dto) {
        ItemSaude novoItem = saudeService.criarItem(dto, null); // Passa null para indicar que é global
        return ResponseEntity.status(HttpStatus.CREATED).body(novoItem);
    }

    @PostMapping("/registros")
    @Operation(summary = "Registrar aplicação de saúde no pet",
            description = "Vincula um pet a um item de saúde em uma data específica (ex: 'Pet Rex tomou a vacina V10 em 26/10/2025').")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Aplicação registrada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RegistroSaude.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos (ex: IDs nulos, data nula)", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
            @ApiResponse(responseCode = "403", description = "Usuário não é o dono do pet", content = @Content),
            @ApiResponse(responseCode = "404", description = "Pet ou Item de Saúde não encontrado", content = @Content)
    })
    public ResponseEntity<RegistroSaude> registrarSaude(@RequestBody @Valid RegistroSaudeDTO dto) {
        RegistroSaude novoRegistro = saudeService.registrarSaude(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoRegistro);
    }

    @GetMapping("/registros/{petUuid}")
    @Operation(summary = "Listar histórico de saúde do pet",
            description = "Retorna todos os registros de saúde (vacinas, etc.) de um pet específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Histórico de saúde retornado",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = RegistroSaude.class)))),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
            @ApiResponse(responseCode = "403", description = "Usuário não é o dono do pet", content = @Content),
            @ApiResponse(responseCode = "404", description = "Pet não encontrado", content = @Content)
    })
    public ResponseEntity<List<RegistroSaude>> getRegistrosDoPet(
            @Parameter(description = "UUID do pet para buscar os registros de saúde", required = true)
            @PathVariable UUID petUuid) {
        List<RegistroSaude> registros = saudeService.getRegistrosPorPet(petUuid);
        return ResponseEntity.ok(registros);
    }

    // utilitário para pegar o tutor logado a partir do token
    private Tutor getTutorLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return tutorService.buscarTutorPorEmail(email);
    }
}
