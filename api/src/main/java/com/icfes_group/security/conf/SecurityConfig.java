package com.icfes_group.security.conf;

import com.icfes_group.security.filters.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable()) // Desactiva CSRF (no lo necesitas si usas JWT en APIs REST)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Establece la sesión como stateless
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/error").permitAll() 
                .requestMatchers("/usuarios", "/usuarios/inicio-sesion").permitAll() // Rutas públicas
                .requestMatchers("/usuarios/active").hasRole("Decano")
                .requestMatchers("/personas","/personas/*").permitAll()
                .requestMatchers("/resultados/file").hasAnyRole("Decano","Director de Programa","Coordinador de Saber Pro")
                .anyRequest().authenticated() // El resto requiere JWT válido
            )
            .httpBasic().disable()  // Desactiva HTTP Basic
            .formLogin().disable()  // Desactiva el formulario de login por defecto
            .exceptionHandling(ex -> 
                ex.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)) // Responde con 401 sin la cabecera Basic
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class) // Agrega el filtro JWT
            .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Encriptación de contraseñas para usuarios
    }
}
