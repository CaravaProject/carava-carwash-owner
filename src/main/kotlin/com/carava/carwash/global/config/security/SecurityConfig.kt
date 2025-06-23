package com.carava.carwash.global.config.security

import com.carava.carwash.customer.service.CustomerUserDetailsService
import com.carava.carwash.owner.service.OwnerUserDetailsService
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
    private val customerUserDetailsService: CustomerUserDetailsService,
    private val ownerUserDetailsService: OwnerUserDetailsService
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    @Primary
    fun defaultAuthenticationManager(config: AuthenticationConfiguration): AuthenticationManager {
        return config.authenticationManager
    }

    @Bean("customerAuthenticationManager")
    fun customerAuthenticationManager(): AuthenticationManager{
        val provider = DaoAuthenticationProvider(customerUserDetailsService).apply {
            setPasswordEncoder(passwordEncoder())
        }

        return ProviderManager(listOf(provider))
    }

    @Bean("ownerAuthenticationManager")
    fun ownerAuthenticationManager(): AuthenticationManager{
        val provider = DaoAuthenticationProvider(ownerUserDetailsService).apply {
            setPasswordEncoder(passwordEncoder())
        }

        return ProviderManager(listOf(provider))
    }

    @Bean
    fun customerFilterChain(http: HttpSecurity, @Qualifier("customerAuthenticationManager") authManager: AuthenticationManager): SecurityFilterChain {
        return http
            .securityMatcher("/api/customer/**")
            .authenticationManager(authManager)
            .csrf { it.disable() }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/api/customer/auth/signin", "/api/customer/auth/signup").permitAll()
                    .anyRequest().authenticated()
            }
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .build()
    }

    @Bean
    fun ownerFilterChain(http: HttpSecurity, @Qualifier("ownerAuthenticationManager") authManager: AuthenticationManager): SecurityFilterChain {
        return http
            .securityMatcher("/api/owner/**")
            .authenticationManager(authManager)
            .csrf { it.disable() }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/api/owner/auth/signin", "/api/owner/auth/signup").permitAll()
                    .anyRequest().authenticated()
            }
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .build()
    }

}