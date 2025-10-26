package br.csi.projeto_calculo_racao.controller;


import br.csi.projeto_calculo_racao.DTO.DadosAutenticacaoDTO;
import br.csi.projeto_calculo_racao.DTO.DadosUsuarioTokenDTO;
import br.csi.projeto_calculo_racao.service.TokenServiceJWT;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
@Tag(name = "Autenticação", description = "Endpoint para obter o token de acesso (JWT)")
public class AutenticacaoController {

    private final AuthenticationManager manager;
    private final TokenServiceJWT tokenService;

    public AutenticacaoController(AuthenticationManager manager, TokenServiceJWT tokenService) {
        this.manager = manager;
        this.tokenService = tokenService;
    }

    @PostMapping
    @Operation(summary = "Realizar Login", description = "Autentica um usuário e retorna um token JWT.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login bem-sucedido, token retornado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DadosUsuarioTokenDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos (ex: email mal formatado)", content = @Content), // Via MethodArgumentNotValidException
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas (usuário ou senha incorretos)", content = @Content) // Via BadCredentialsException
    })
    public ResponseEntity<DadosUsuarioTokenDTO> login( @RequestBody @Valid DadosAutenticacaoDTO dados) {
        var authenticationToken = new UsernamePasswordAuthenticationToken(dados.email(), dados.senha());
        Authentication authentication = this.manager.authenticate(authenticationToken);

        var userDetails = (UserDetails) authentication.getPrincipal();
        var tokenJWT = tokenService.gerarToken(userDetails);

        return ResponseEntity.ok(new DadosUsuarioTokenDTO (tokenJWT));
    }
}
