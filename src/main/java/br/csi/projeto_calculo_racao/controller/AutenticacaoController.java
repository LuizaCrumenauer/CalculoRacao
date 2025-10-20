package br.csi.projeto_calculo_racao.controller;


import br.csi.projeto_calculo_racao.DTO.DadosAutenticacaoDTO;
import br.csi.projeto_calculo_racao.DTO.DadosUsuarioTokenDTO;
import br.csi.projeto_calculo_racao.service.TokenServiceJWT;
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
public class AutenticacaoController {

    private final AuthenticationManager manager;
    private final TokenServiceJWT tokenService;

    public AutenticacaoController(AuthenticationManager manager, TokenServiceJWT tokenService) {
        this.manager = manager;
        this.tokenService = tokenService;
    }

    @PostMapping
    public ResponseEntity<DadosUsuarioTokenDTO> login( @RequestBody @Valid DadosAutenticacaoDTO dados) {
        var authenticationToken = new UsernamePasswordAuthenticationToken(dados.email(), dados.senha());
        Authentication authentication = this.manager.authenticate(authenticationToken);

        var userDetails = (UserDetails) authentication.getPrincipal();
        var tokenJWT = tokenService.gerarToken(userDetails);

        return ResponseEntity.ok(new DadosUsuarioTokenDTO (tokenJWT));
    }
}
