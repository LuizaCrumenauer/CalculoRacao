package br.csi.projeto_calculo_racao.controller;

import br.csi.projeto_calculo_racao.DTO.ItemSaudeDTO;
import br.csi.projeto_calculo_racao.DTO.RegistroSaudeDTO;
import br.csi.projeto_calculo_racao.model.registroSaude.ItemSaude;
import br.csi.projeto_calculo_racao.model.registroSaude.RegistroSaude;
import br.csi.projeto_calculo_racao.model.tutor.Tutor;
import br.csi.projeto_calculo_racao.model.usuario.Usuario;
import br.csi.projeto_calculo_racao.service.SaudeService;
import br.csi.projeto_calculo_racao.service.TutorService;
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

    // Endpoint para um Tutor listar os itens de saúde disponíveis (globais + seus) e admin ver todos
    @GetMapping("/itens/listar")
    public ResponseEntity<List<ItemSaude>> getItensDisponiveis() {
        // Pega os dados de autenticação do usuário logado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Verifica se entre as permissões (authorities) do usuário existe a "ROLE_ADMIN"
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

    // Endpoint para um Tutor criar um novo item de saúde personalizado
    @PostMapping("/itens")
    public ResponseEntity<ItemSaude> criarItemPersonalizado(@RequestBody @Valid ItemSaudeDTO dto) {
        Tutor tutorLogado = getTutorLogado(); // Associa o item ao tutor logado
        ItemSaude novoItem = saudeService.criarItem(dto, tutorLogado);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoItem);
    }

    // Endpoint para um ADMIN criar um novo item de saúde GLOBAL
    @PostMapping("/itens/admin")
    public ResponseEntity<ItemSaude> criarItemGlobal(@RequestBody @Valid ItemSaudeDTO dto) {
        ItemSaude novoItem = saudeService.criarItem(dto, null); // Passa null para indicar que é global
        return ResponseEntity.status(HttpStatus.CREATED).body(novoItem);
    }

    // Endpoint para registrar a aplicação de um item de saúde em um pet
    @PostMapping("/registros")
    public ResponseEntity<RegistroSaude> registrarSaude(@RequestBody @Valid RegistroSaudeDTO dto) {
        RegistroSaude novoRegistro = saudeService.registrarSaude(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoRegistro);
    }

    // Endpoint para listar todos os registros de saúde de um pet
    @GetMapping("/registros/{petUuid}")
    public ResponseEntity<List<RegistroSaude>> getRegistrosDoPet(@PathVariable UUID petUuid) {
        List<RegistroSaude> registros = saudeService.getRegistrosPorPet(petUuid);
        return ResponseEntity.ok(registros);
    }

    // Mtodo utilitário para pegar o tutor logado a partir do token
    private Tutor getTutorLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return tutorService.buscarTutorPorEmail(email);
    }
}
