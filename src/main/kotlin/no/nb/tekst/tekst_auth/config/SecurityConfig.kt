package no.nb.tekst.tekst_auth.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain

@Configuration
@EnableWebFluxSecurity
class SecurityConfig {

    @Bean
    fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        http.csrf { csrf -> csrf.disable() }.authorizeExchange { exchange ->
            exchange
                .pathMatchers(
                    "/",
                    "/webjars/swagger-ui/**",
                    "/v3/api-docs",
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/swagger-resources",
                    "/swagger-resources/**",
                    "/actuator",
                    "/actuator/**"
                ).permitAll()
                .pathMatchers(HttpMethod.GET, "/**").permitAll()
                .pathMatchers(HttpMethod.POST, "/v1/auth/**").permitAll()
        }
        return http.build()
    }
}