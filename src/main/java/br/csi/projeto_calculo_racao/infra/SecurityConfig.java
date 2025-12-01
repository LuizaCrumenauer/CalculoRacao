package br.csi.projeto_calculo_racao.infra;

import br.csi.projeto_calculo_racao.model.usuario.Role;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AutenticacaoFilter autenticacaoFilter;

    public SecurityConfig(AutenticacaoFilter autenticacaoFilter) {
        this.autenticacaoFilter = autenticacaoFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Swagger + API docs
                        .requestMatchers(
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**"
                        ).permitAll()
                        .requestMatchers(HttpMethod.POST, "/login", "/tutores/cadastrar").permitAll()
                        // Endpoints de ADMIN
                        .requestMatchers(HttpMethod.GET, "/tutores/listar").hasRole(Role.ADMIN.name())
                        .requestMatchers("/usuarios/tornar-admin/**").hasRole(Role.ADMIN.name())
                        .requestMatchers(HttpMethod.POST, "/usuarios/admin").hasRole(Role.ADMIN.name())
                        .requestMatchers(HttpMethod.PUT, "/usuarios/admin/atualizar").hasRole(Role.ADMIN.name())
                        .requestMatchers(HttpMethod.POST, "/saude/itens/admin").hasRole(Role.ADMIN.name())
                        .requestMatchers(HttpMethod.GET, "/pets/listar").hasRole(Role.ADMIN.name())
                        .requestMatchers(HttpMethod.DELETE, "/usuarios/{usuarioId:\\d+}").hasRole( Role.ADMIN.name())
                        // Usuários autenticados
                        .requestMatchers(HttpMethod.DELETE, "/usuarios/minha-conta").authenticated()
                        .requestMatchers(HttpMethod.GET, "/tutores/perfil").authenticated()
                        .requestMatchers("/racoes/listar").authenticated()
                        .requestMatchers("/saude/**").authenticated()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint())
                        .accessDeniedHandler(accessDeniedHandler())
                )
                .addFilterBefore(autenticacaoFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Permite requisições do seu Angular (localhost:4200)
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        // Permite os métodos HTTP comuns
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "TRACE", "CONNECT"));
        // Permite todos os cabeçalhos (incluindo Authorization)
        configuration.setAllowedHeaders( List.of("*"));
        // Permite credenciais (se necessário no futuro)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            var erro = new DadosErroValidacao(null, "Usuário não autenticado ou token inválido.");
            new ObjectMapper().writeValue(response.getWriter(), erro);
        };
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json;charset=UTF-8");
            var erro = new DadosErroValidacao(null, "Acesso negado. Você não tem permissão para acessar este recurso.");
            new ObjectMapper().writeValue(response.getWriter(), erro);
        };
    }

    private record DadosErroValidacao(String campo, String mensagem) {}

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
