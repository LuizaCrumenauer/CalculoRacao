package br.csi.projeto_calculo_racao.infra;

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
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


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
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/login").permitAll()
                        // Permitir o novo endpoint de cadastro de tutor
                        .requestMatchers(HttpMethod.POST, "/tutores/cadastrar").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        // Apenas ADMINS podem listar todos os tutores
                        .requestMatchers(HttpMethod.GET, "/tutores/listar").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/tutores/perfil").authenticated()
                        // Protegendo o endpoint de admin
                        .requestMatchers("/usuarios/tornar-admin/**").hasRole("ADMIN")
                        // Apenas ADMINS podem criar outros ADMINS (cria apenas Usuario)
                        .requestMatchers(HttpMethod.POST, "/usuarios/admin").hasRole("ADMIN")
                        // Apenas ADMINS podem criar itens de saúde GLOBAIS
                        .requestMatchers(HttpMethod.POST, "/saude/itens/admin").hasRole("ADMIN")
                        // Todas as outras rotas de saúde exigem apenas um usuário logado
                        .requestMatchers("/saude/**").authenticated()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(autenticacaoFilter, UsernamePasswordAuthenticationFilter.class) //primeiro verifica usuario (autenticacaoFilter) para depois bloquear ou nao a requisição
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
