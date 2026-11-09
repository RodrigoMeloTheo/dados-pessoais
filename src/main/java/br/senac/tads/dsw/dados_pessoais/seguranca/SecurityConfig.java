package br.senac.tads.dsw.dados_pessoais.seguranca;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.access.prepost.PreAuthorize;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // habilita @PreAuthorize e @PostAuthorize nos controllers
public class SecurityConfig {

    // injetando
    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    // Define a cadeia de filtros HTTP que o Spring Security aplica em cada
    // requisição.
    // A ordem das regras em authorizeHttpRequests importa: a primeira regra que
    // casar é aplicada.
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Desabilita CSRF — não é necessário em APIs REST stateless
                // (CSRF protege formulários HTML com sessão; aqui usaremos JWT por requisição)
                .csrf(csrf -> csrf.disable())

                // Permite renderização em frames — necessário para o console H2 funcionar no
                // navegador
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))

                // Define a política de sessão como STATELESS: o servidor não cria nem usa
                // sessão HTTP.
                // Cada requisição deve ser autenticada de forma independente (via JWT no
                // header).
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // ---- Regras de autorização por URL ----
                .authorizeHttpRequests(auth -> auth

                        // Endpoint de login: sempre público (é aqui que o usuário obtém o token)
                        .requestMatchers(HttpMethod.POST, "/login").permitAll()

                        // Console H2: público durante o desenvolvimento (REMOVA em produção!)
                        .requestMatchers("/h2-console/**").permitAll()

                        // Swagger UI: público para facilitar testes
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // /me e os endpoints de acesso exigem autenticação (cobertos por anyRequest)
                        .requestMatchers("/me").authenticated()
                        // Permitir acesso aos arquivos das telas - antecipando config da etapa 7.7
                        .requestMatchers("/*.html", "/*.css", "/*.js").permitAll()

                        // Leitura de pessoas: público
                        .requestMatchers(HttpMethod.GET, "/pessoas", "/pessoas/**").permitAll()
                        // Escrita exige autenticação (o @PreAuthorize no controller exigirá ROLE_ADMIN)
                        .requestMatchers(HttpMethod.POST, "/pessoas", "/pessoas/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/pessoas/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/pessoas/**").authenticated()

                        // Demais endpoints precisam de autenticação - MANTER SEMPRE COMO ÚLTIMA
                        // CONFIGURAÇÃO
                        .anyRequest().authenticated()

                )

                // Registra o JwtFilter ANTES do filtro padrão de autenticação por
                // username/senha.
                // Fluxo: JwtFilter extrai e valida o token → preenche o SecurityContextHolder
                // → os demais filtros e regras de autorização enxergam o usuário autenticado.
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // AuthenticationManager: componente responsável por executar a autenticação.
    // DaoAuthenticationProvider é a implementação que usa um UserDetailsService
    // (nosso
    // UsuarioSistemaService) para buscar o usuário e um PasswordEncoder para
    // comparar a senha.
    // OBS: Esse código abaixo é válido para o Spring Boot 4 em diante.
    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(provider);
    }

    // PasswordEncoder: define como as senhas são codificadas e comparadas.
    // ATENÇÃO: NoOpPasswordEncoder armazena senhas em TEXTO PURO.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
